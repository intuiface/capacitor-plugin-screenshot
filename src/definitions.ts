export interface CapacitorScreenshotPlugin {
  getScreenshot(options: ScreenshotOptions): Promise<{ base64: string, URI: string } | null>;
}

export interface ScreenshotOptions {
  quality: number
}