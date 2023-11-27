package com.arnyminerz.filamagenta.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.getBooleanState
import com.arnyminerz.filamagenta.storage.getStringState
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.screen.LoadingScreen
import com.arnyminerz.filamagenta.ui.screen.LoginScreen
import com.arnyminerz.filamagenta.ui.screen.MainScreen
import com.arnyminerz.filamagenta.ui.screen.model.IntroScreen

@Composable
fun MainComposable(
    isAddingNewAccount: Boolean
) {
    var navigator: Navigator? by remember { mutableStateOf(null) }

    val accountsList by accounts.getAccountsLive().collectAsState()

    val selectedAccount by settings.getStringState(SettingsKeys.SELECTED_ACCOUNT, "")
    val isAdmin = accountsList?.find { it.name == selectedAccount }?.let { accounts.isAdmin(it) }
    val shownAdmin by settings.getBooleanState(SettingsKeys.SYS_SHOWN_ADMIN, false)

    fun handleNavigation() {
        val accounts = accountsList ?: return

        if (accounts.isEmpty()) {
            navigator?.push(LoginScreen)
        } else if (isAdmin == true && !shownAdmin) {
            navigator?.push(IntroScreen)
        } else {
            navigator?.push(MainScreen)
        }
    }

    LaunchedEffect(isAddingNewAccount, isAdmin, shownAdmin) {
        handleNavigation()
    }

    Navigator(
        screen = LoadingScreen
    ) {
        navigator = it

        CurrentScreen()
    }
}
