import androidx.compose.ui.window.ComposeUIViewController
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.cache.DriverFactory
import com.arnyminerz.filamagenta.cache.createDatabase
import com.arnyminerz.filamagenta.diagnostics.SentryDiagnostics
import com.arnyminerz.filamagenta.lifecycle.updateLocale
import com.arnyminerz.filamagenta.states.Action
import com.arnyminerz.filamagenta.states.createStore
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.storage.settingsFactory
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val store = CoroutineScope(SupervisorJob()).createStore()

fun MainViewController() = ComposeUIViewController {
    settingsFactory = SettingsFactoryProvider().factory

    SentryDiagnostics.initialize()

    updateLocale()

    AccountsProvider().provide()

    createDatabase(DriverFactory())

    AppTheme {
        MainScreen {
            // there's no way of closing apps when pressing back on ios
        }
    }
}

fun onBackGesture() {
    store.send(Action.OnBackPressed)
}

fun isDataCollectionEnabled(): Boolean {
    // Make sure the factory is initialized
    settingsFactory = SettingsFactoryProvider().factory
    return settings.getBoolean(SettingsKeys.DATA_COLLECTION, true)
}
