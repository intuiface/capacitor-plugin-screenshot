package com.intuiface.plugins.screenshot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.DisplayMetrics;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@CapacitorPlugin(name = "CapacitorScreenshot", requestCodes = { 1 })
public class CapacitorScreenshotPlugin extends Plugin {

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    private ActivityResultLauncher<Intent> mediaProjectionActivityLauncher;

    private PluginCall savedCall;
    private VirtualDisplay virtualDisplay;
    private Handler handler;

    @Override
    public void load() {
        HandlerThread handlerThread = new HandlerThread("ScreenshotThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        mediaProjectionActivityLauncher =
            getActivity()
                .registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                assert result.getData() != null;
                                mediaProjection = mediaProjectionManager.getMediaProjection(result.getResultCode(), result.getData());
                                startScreenshotCapture(mediaProjection);
                            }
                        }
                    }
                );
    }

    @PluginMethod
    public void getScreenshot(PluginCall call) {
        if (mediaProjection != null && virtualDisplay != null) {
            savedCall = call;

            final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
            // Create an ImageReader to capture the screen content
            ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
            // Handle the captured images from the ImageReader
            imageReader.setOnImageAvailableListener(
                reader -> {
                    Image image = imageReader.acquireLatestImage();
                    if (image != null) {
                        // Process the captured image
                        processScreenshot(image);
                        // Release the image resources
                        image.close();
                    }
                },
                handler
            );

            // set the new surface to get the capture
            virtualDisplay.setSurface(imageReader.getSurface());
        } else {
            ScreenCaptureManager screenCaptureManager = new ScreenCaptureManager(getContext());
            screenCaptureManager.startForeground();

            getBridge()
                .getActivity()
                .runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            Activity activity = getBridge().getActivity();
                            if (activity != null) {
                                mediaProjectionManager =
                                    (MediaProjectionManager) activity.getSystemService(Activity.MEDIA_PROJECTION_SERVICE);

                                savedCall = call;
                                Intent projectionIntent = mediaProjectionManager.createScreenCaptureIntent();
                                mediaProjectionActivityLauncher.launch(projectionIntent);
                            } else {
                                call.reject("Activity is null");
                            }
                        }
                    }
                );
        }
    }

    private void startScreenshotCapture(MediaProjection mediaProjection) {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        int screenDensity = metrics.densityDpi;

        // Create an ImageReader to capture the screen content
        ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);

        mediaProjection.registerCallback(new MediaProjection.Callback() {}, null);
        // Create a VirtualDisplay using the mediaProjection and imageReader
        this.virtualDisplay =
            mediaProjection.createVirtualDisplay(
                "ScreenCapture",
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,
                handler
            );
    }

    private void processScreenshot(Image image) {
        if (savedCall != null) {
            // Convert the Image to a Bitmap
            Bitmap bitmap = convertImageToBitmap(image);

            // Save or send the bitmap to your server
            int quality = savedCall.getInt("quality", 100);

            JSObject ret = new JSObject();
            String base64Image = convertBitmapToBase64(bitmap, quality);

            // save the bitmap as file
            String filename = savedCall.getString("name", "screenshot");
            File file = new File(getContext().getFilesDir(), filename + ".png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ret.put("base64", "data:image/png;base64," + base64Image);
            ret.put("URI", file.toURI());
            savedCall.resolve(ret);

            // Release any resources
            bitmap.recycle();
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap convertImageToBitmap(Image image) {
        if (image == null) {
            return null;
        }

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();

        int width = image.getWidth();
        int height = image.getHeight();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;

        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);

        // generate the final bitmap at the right size from the bitmap created
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

        int desiredWidth = savedCall.getInt("size", width);
        // scale but keep ratio
        int scaledWidth = Math.min(width, desiredWidth);
        int scaledHeight = height * scaledWidth / width;
        bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false);

        return bitmap;
    }
}
