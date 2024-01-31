# capacitor-plugin-screenshot

Capacitor plugin to take screenshot for iOS and Android devices.

The main goal of this plugin is to be able to take a screenshot of the application exactly as you see it. Even if there are playing videos, an iframe with some content, a cross-origin CSS...etc.

This plugin uses the `takeSnapshot` method of the `WKWebView` for iOS : https://developer.apple.com/documentation/webkit/wkwebview/2873260-takesnapshot
And for Android, the `MediaProjection` API : https://developer.android.com/reference/android/media/projection/MediaProjection

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

iOS version 11+ is supported.

Nothing more to do, it should work by calling the `getScreenshot` function.


## Android

Android Version 6+ is supported.

To be able to take screenshot on Android, you have to declare a foreground service in the `AndroidManifest.xml` in the application tag :

```xml
<service android:enabled="true" android:foregroundServiceType="mediaProjection" android:name="com.intuiface.plugins.screenshot.ScreenCaptureService" />
```

The foreground service will ask you to cast your screen and this is mandatory to take screenshot with the `MediaProjection` API.
