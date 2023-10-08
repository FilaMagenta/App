import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import states.Action

val store = CoroutineScope(SupervisorJob()).createStore()

fun MainViewController() = ComposeUIViewController { App() }

fun onBackGesture() {
    store.send(Action.OnBackPressed)
}
