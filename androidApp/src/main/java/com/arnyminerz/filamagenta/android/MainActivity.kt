package com.arnyminerz.filamagenta.android

import android.app.Activity
import android.content.res.Configuration
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.dialog.UpdateDialog
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.state.MainViewModel
import com.arnyminerz.filamagenta.ui.theme.AppTheme
import com.arnyminerz.filamagenta.utils.Language
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.updatePriority
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.set
import dev.icerock.moko.resources.desc.StringDesc
import io.github.aakira.napier.Napier
import io.sentry.compose.SentryTraced

@OptIn(ExperimentalComposeUiApi::class)
class MainActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_NEW_ACCOUNT = "new_account"

        /**
         * From which app update priority the update should be forced.
         */
        const val APP_UPDATE_PRIORITY_MAJOR = 4
    }

    private val viewModel by viewModels<Model>()
    private val mainViewModel by viewModels<MainViewModel>()

    private var languageChangeListener: SettingsListener? = null

    private val immediateAppUpdateResultLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            Napier.i("A major update was cancelled. Closing app...")
            finish()
        }
    }

    private val flexibleAppUpdateResultLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
    }

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

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
            SentryTraced(tag = "MainActivity") {
                AppTheme {
                    val showAppUpdateDialog by viewModel.showAppUpdateDialog.observeAsState(false)
                    if (showAppUpdateDialog) {
                        UpdateDialog(
                            onInstallRequest = { appUpdateManager.completeUpdate() },
                            onDismissRequest = { viewModel.showAppUpdateDialog.postValue(false) }
                        )
                    }

                    MainScreen(
                        isAddingNewAccount = intent.getBooleanExtra(EXTRA_NEW_ACCOUNT, false),
                        viewModel = mainViewModel,
                        nfc = nfcData,
                        onApplicationEndRequested = ::finish
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        checkForUpdates()
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

        // Update the stored value
        settings[SettingsKeys.LANGUAGE] = language?.toLanguageTag() ?: Language.System.langCode

        // Update the UI
        StringDesc.localeType =
            language?.toLanguageTag()?.let { StringDesc.LocaleType.Custom(it) } ?: StringDesc.LocaleType.System
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val updateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateInProgress =
                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            val isImmediateUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            val priority = appUpdateInfo.updatePriority
            if (updateAvailable || isUpdateInProgress) {
                if (priority >= APP_UPDATE_PRIORITY_MAJOR && isImmediateUpdateAllowed) {
                    // If priority is greater than 3, it's a major version, force update
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        immediateAppUpdateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                } else {
                    val listener = InstallStateUpdatedListener { state ->
                        if (state.installStatus() == InstallStatus.DOWNLOADED) {
                            viewModel.showAppUpdateDialog.postValue(true)
                        }
                    }
                    appUpdateManager.registerListener(listener)

                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        flexibleAppUpdateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .build()
                    )

                    appUpdateManager.unregisterListener(listener)
                }
            }
        }
    }

    class Model : ViewModel() {
        val showAppUpdateDialog = MutableLiveData(false)
    }
}
