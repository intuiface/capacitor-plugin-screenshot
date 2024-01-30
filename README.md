# capacitor-plugin-screenshot

Capacitor plugin to take screenshot for iOS and Android devices

## Install

```bash
npm install capacitor-plugin-screenshot
npx cap sync
```

## API

<docgen-index>

* [`getScreenshot(...)`](#getscreenshot)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getScreenshot(...)

```typescript
getScreenshot(options: ScreenshotOptions) => Promise<ScreenshotValue | null>
```

Function to take a screenshot

| Param         | Type                                                            | Description                                       |
| ------------- | --------------------------------------------------------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#screenshotoptions">ScreenshotOptions</a></code> | : options with quality desired for the screenshot |

**Returns:** <code>Promise&lt;<a href="#screenshotvalue">ScreenshotValue</a> | null&gt;</code>

--------------------


### Interfaces


#### ScreenshotValue

| Prop         | Type                | Description                                      |
| ------------ | ------------------- | ------------------------------------------------ |
| **`base64`** | <code>string</code> | The base64 string of the screenshot. Can be null |


#### ScreenshotOptions

| Prop          | Type                | Description                                 |
| ------------- | ------------------- | ------------------------------------------- |
| **`quality`** | <code>number</code> | The quality of the screenshot between 0-100 |

</docgen-api>

---

## iOS

Nothing more to do, it should work by calling the `getScreenshot` function.

## Android

To be able to take screenshot on Android, you have to declare a foreground service in the `AndroidManifest.xml` in the application tag :

```xml
<service android:enabled="true" android:foregroundServiceType="mediaProjection" android:name="com.intuiface.plugins.screenshot.ScreenCaptureService" />
```

The foreground service will ask you to cast your screen and this is mandatory to take screenshot with this plugin as we use the Media Projection API (https://developer.android.com/reference/android/media/projection/MediaProjection) of Android.
