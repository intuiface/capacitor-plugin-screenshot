export interface CapacitorScreenshotPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
