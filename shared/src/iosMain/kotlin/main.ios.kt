import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.arnyminerz.filamagenta.account.AccountsProvider
import com.arnyminerz.filamagenta.cache.DriverFactory
import com.arnyminerz.filamagenta.cache.createDatabase
import com.arnyminerz.filamagenta.states.Action
import com.arnyminerz.filamagenta.states.createStore
import com.arnyminerz.filamagenta.storage.SettingsFactoryProvider
import com.arnyminerz.filamagenta.storage.settingsFactory
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val store = CoroutineScope(SupervisorJob()).createStore()

fun MainViewController() = ComposeUIViewController {
    LaunchedEffect(Unit) {
        settingsFactory = SettingsFactoryProvider().factory

        AccountsProvider().provide()

        createDatabase(DriverFactory())
    }

    AppTheme {
        MainScreen {
            // there's no way of closing apps when pressing back on ios
        }
    }
}

fun onBackGesture() {
    store.send(Action.OnBackPressed)
}
