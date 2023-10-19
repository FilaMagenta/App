package com.arnyminerz.filamagenta.android

import android.content.res.Configuration
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.arnyminerz.filamagenta.ui.theme.AppTheme
import com.arnyminerz.filamagenta.utils.Language
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier

class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NEW_ACCOUNT = "new_account"
    }

    private val viewModel by viewModels<MainViewModel>()

    private var languageChangeListener: SettingsListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateAppLocale()

        languageChangeListener = settings.addStringListener(SettingsKeys.LANGUAGE, Language.System.langCode) { code ->
            Napier.i("Updated app locale preference. Notifying compat...")
            if (code == Language.System.langCode) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.getEmptyLocaleList()
                )
            } else {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(code)
                )
            }
        }

        Napier.d("Intent action: ${intent.action}", tag = "MainActivity")
        Napier.d("Intent data: ${intent.data}", tag = "MainActivity")

        val nfcData = if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            intent.dataString
        } else {
            null
        }

        setContent {
            AppTheme {
                MainScreen(
                    isAddingNewAccount = intent.getBooleanExtra(EXTRA_NEW_ACCOUNT, false),
                    viewModel = viewModel,
                    nfc = nfcData
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
