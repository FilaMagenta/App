package com.arnyminerz.filamagenta.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.BadgeDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.ui.navigation.NavigationBarItem
import com.arnyminerz.filamagenta.ui.navigation.NavigationBarScaffold
import com.arnyminerz.filamagenta.ui.page.EventsPage
import dev.icerock.moko.resources.compose.stringResource

/**
 * Once logged in, this is the first screen shown to the user.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppScreen(isAdmin: Boolean) {
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
                label = { stringResource(MR.strings.nav_events) },
                content = { EventsPage(isAdmin) }
            ),
            NavigationBarItem(
                icon = Icons.Outlined.Settings,
                label = { stringResource(MR.strings.nav_settings) }
            ) {
                Text("Settings")
            }
        ),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(MR.strings.app_name))
                        if (isAdmin) {
                            Text(
                                text = "admin",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.contentColorFor(BadgeDefaults.containerColor),
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BadgeDefaults.containerColor)
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            )
        }
    )
}
