package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.ui.navigation.NavigationBarItem
import com.arnyminerz.filamagenta.ui.navigation.NavigationBarScaffold
import dev.icerock.moko.resources.compose.stringResource

/**
 * Once logged in, this is the first screen shown to the user.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppScreen() {
    NavigationBarScaffold(
        items = listOf(
            NavigationBarItem(
                icon = Icons.Outlined.Wallet,
                label = { stringResource(MR.strings.nav_wallet) }
            ) {
                Text("Wallet")
            },
            NavigationBarItem(
                icon = Icons.Outlined.CalendarToday,
                label = { stringResource(MR.strings.nav_events) }
            ) {
                Text("Events")
            },
            NavigationBarItem(
                icon = Icons.Outlined.Settings,
                label = { stringResource(MR.strings.nav_settings) }
            ) {
                Text("Settings")
            }
        ),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(MR.strings.app_name)) }
            )
        }
    )
}
