package com.arnyminerz.filamagenta.android

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.arnyminerz.filamagenta.ui.theme.AppTheme
import com.arnyminerz.filamagenta.utils.Language
import com.russhwolf.settings.get
import com.russhwolf.settings.set

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NEW_ACCOUNT = "new_account"
    }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateAppLocale()

        setContent {
            AppTheme {
                MainScreen(
                    intent.getBooleanExtra(EXTRA_NEW_ACCOUNT, false),
                    viewModel
                ) {
                    finish()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        updateAppLocale()
    }

    /**
     * Updates the configuration with the application's selected language.
     */
    private fun updateAppLocale() {
        val locales = AppCompatDelegate.getApplicationLocales()
        val language = locales[0]
        settings[SettingsKeys.LANGUAGE] = language?.toLanguageTag() ?: Language.System.langCode
    }
}
