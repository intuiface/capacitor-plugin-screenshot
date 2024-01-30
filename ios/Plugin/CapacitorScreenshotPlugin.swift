import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorScreenshotPlugin)
public class CapacitorScreenshotPlugin: CAPPlugin {
    private let implementation = CapacitorScreenshot()

    @objc func getScreenshot(_ call: CAPPluginCall) {

        let quality = ((call.getDouble("quality") ?? 100) / 100)
        DispatchQueue.main.async {
            let config = WKSnapshotConfiguration()
            config.rect = self.webView!.frame
            config.afterScreenUpdates = false
            self.webView?.takeSnapshot(with: config, completionHandler: { (image: UIImage?, error: Error?) in
                if error != nil {
                    return
                }
                guard let imageData = image!.jpegData(compressionQuality: quality) else {return}
                let base64Result = imageData.base64EncodedString()
                let file = "data:image/png;base64," + base64Result

                call.resolve([
                    "base64": base64Result,
                    "URI": file
                ])
            })
        }
    }
}
