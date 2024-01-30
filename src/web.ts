import { WebPlugin } from '@capacitor/core';

import type { CapacitorScreenshotPlugin } from './definitions';

export class CapacitorScreenshotWeb
  extends WebPlugin
  implements CapacitorScreenshotPlugin
{
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
