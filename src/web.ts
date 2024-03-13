import { WebPlugin } from '@capacitor/core';

import type {
  CapacitorScreenshotPlugin,
  ScreenshotOptions,
  ScreenshotValue,
} from './definitions';

export class CapacitorScreenshotWeb
  extends WebPlugin
  implements CapacitorScreenshotPlugin
{
  private captureStream: any;
  private videoCapture: any;
  private captureCanvas: any;

  async getScreenshot(
    options: ScreenshotOptions,
  ): Promise<ScreenshotValue | null> {
    try {
      if (!this.captureStream) {
        // display a message ?

        // get the media device
        const mediaDevices = navigator.mediaDevices as any;
        const width = screen.width * (window.devicePixelRatio || 1);
        const height = screen.height * (window.devicePixelRatio || 1);

        // start sharing screen (ask for it)
        this.captureStream = await mediaDevices.getDisplayMedia({
          preferCurrentTab: true,
          audio: false,
          video: {
            width,
            height,
          },
        });
        // create a video tag
        this.videoCapture = document.createElement('video');
        // create a canvas
        this.captureCanvas = document.createElement('canvas');
      }

      return new Promise<any>(resolve => {
        const callbackLoadedMetadata = () => {
          // unbind from loaded metadata
          this.videoCapture.removeEventListener(
            'loadedmetadata',
            callbackLoadedMetadata,
          );
          // set the canvas size with the video size
          this.captureCanvas.width = this.videoCapture.videoWidth;
          this.captureCanvas.height = this.videoCapture.videoHeight;
          let newWidth = this.captureCanvas.width;
          let newHeight = this.captureCanvas.height;
          if (options.size) {
            newWidth = options.size;
            newHeight =
              (this.captureCanvas.height * newWidth) / this.captureCanvas.width;
            this.captureCanvas.width = newWidth;
            this.captureCanvas.height = newHeight;
          }
          // draw the video into canvas
          this.captureCanvas
            .getContext('2d')
            .drawImage(this.videoCapture, 0, 0, newWidth, newHeight);
          let quality = 1.0;
          if (options.quality) {
            quality = options.quality / 100;
          }
          // get the image of the canvas
          const frame = this.captureCanvas.toDataURL('image/jpeg', quality);
          // pause the video
          this.videoCapture.pause();

          // return the image
          resolve({ base64: frame });
        };

        // bind on loaded metadata to draw the video when the video is ready
        this.videoCapture.onloadedmetadata = callbackLoadedMetadata;
        // set the src of the video
        this.videoCapture.srcObject = this.captureStream;
        // play the video
        this.videoCapture.play();
      });
    } catch (err) {
      console.error(`Error: ${err as string}`);
      return null;
    }
  }
}
