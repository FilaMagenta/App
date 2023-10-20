package com.arnyminerz.filamagenta.ui.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.cache.AdminTickets
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.ui.theme.ExtendedColors
import dev.icerock.moko.resources.compose.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UsersModalBottomSheet(usersList: List<Pair<Boolean, AdminTickets>>, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        ) {
            stickyHeader(
                key = "title"
            ) {
                Text(
                    text = stringResource(MR.strings.event_screen_admin_scanner_list),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().background(BottomSheetDefaults.ContainerColor)
                )
            }

            items(
                items = usersList,
                key = { (_, ticket) -> ticket.orderId }
            ) { (hasValidated, ticket) ->
                Row(
                    modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasValidated) Icons.Rounded.Check else Icons.Rounded.Close,
                        // todo - content description
                        contentDescription = null,
                        tint = (if (hasValidated) ExtendedColors.Positive else ExtendedColors.Negative).color()
                    )
                    Text(
                        text = ticket.customerName,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    )
                    AnimatedVisibility(
                        visible = !hasValidated
                    ) {
                        IconButton(
                            onClick = {
                                Cache.updateIsValidated(
                                    orderId = ticket.orderId,
                                    isValidated = true
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                // todo - content description
                                contentDescription = null
                            )
                        }
                    }
                    AnimatedVisibility(
                        visible = hasValidated
                    ) {
                        IconButton(
                            onClick = {
                                Cache.updateIsValidated(
                                    orderId = ticket.orderId,
                                    isValidated = false
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                // todo - content description
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}
