export interface CapacitorScreenshotPlugin {
  /**
   * Function to take a screenshot
   * @param {ScreenshotOptions} options : options with quality desired for the screenshot
   * @returns {Promise<ScreenshotValue | null>} Promise with the base64 string of the image if available, null instead
   */
  getScreenshot(options: ScreenshotOptions): Promise<ScreenshotValue | null>;
}

export interface ScreenshotOptions {
  /**
   * The quality of the screenshot between 0-100
   */
  quality: number;
}

export interface ScreenshotValue {
  /**
   * The base64 string of the screenshot.
   * Can be null
   */
  base64?: string;
}
