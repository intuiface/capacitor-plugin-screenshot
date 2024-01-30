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
getScreenshot(options: ScreenshotOptions) => Promise<{ base64: string; URI: string; } | null>
```

| Param         | Type                                                            |
| ------------- | --------------------------------------------------------------- |
| **`options`** | <code><a href="#screenshotoptions">ScreenshotOptions</a></code> |

**Returns:** <code>Promise&lt;{ base64: string; URI: string; } | null&gt;</code>

--------------------


### Interfaces


#### ScreenshotOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`quality`** | <code>number</code> |

</docgen-api>
