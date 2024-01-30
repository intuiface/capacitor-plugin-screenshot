import { registerPlugin } from '@capacitor/core';

import type { CapacitorScreenshotPlugin } from './definitions';

const CapacitorScreenshot =
  registerPlugin<CapacitorScreenshotPlugin>('CapacitorScreenshot', {
    web: () => import('./web').then(m => new m.CapacitorScreenshotWeb()),
  });

export * from './definitions';
export { CapacitorScreenshot };
