package com.arnyminerz.filamagenta.ui.browser

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.arnyminerz.filamagenta.device.PlatformInformation

actual object CustomBrowserUi {
    private var applicationContext: Context? = null

    /**
     * CustomTabs needs a context for launching intents. Call this from your Application to set this context.
     */
    fun provideApplicationContext(context: Context) {
        applicationContext = context
    }

    /**
     * Launches the given URI in the platform-specific browser. Should throw [UnsupportedOperationException] if not
     * supported by current platform.
     *
     * Check with [PlatformInformation.hasSpecificBrowserUi]
     */
    actual fun launchUri(uri: String) {
        // We can't launch any intent without context
        val context = applicationContext ?: throw UnsupportedOperationException()

        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, Uri.parse(uri))
    }
}