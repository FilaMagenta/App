//import Sentry
import SwiftUI
import shared

@main
struct iOSApp: App {
    /*init() {
        SentrySDK.start { options in
            options.dsn = SentryInformation.shared.SentryDsn
            options.releaseName = SentryInformation.shared.ReleaseName
            options.environment = SentryInformation.shared.IsProduction ? "prod" : "dev"
            options.tracesSampleRate = 1.0
        }
    }*/
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
