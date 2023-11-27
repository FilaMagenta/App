package com.arnyminerz.filamagenta.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.getStringState
import com.arnyminerz.filamagenta.storage.settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Obtains the currently selected account from the list.
 * The preference is based on the account's name stored in settings at [SettingsKeys.SELECTED_ACCOUNT].
 *
 * @return The account currently selected, or `null` if the accounts list is empty, or the selection
 * was not found in the list.
 */
fun List<Account>.getSelected(): Account? {
    if (isEmpty()) return null
    val name = settings.getString(SettingsKeys.SELECTED_ACCOUNT, first().name)
    return find { it.name == name }
}

/**
 * Obtains the currently selected account from the list.
 * The preference is based on the account's name stored in settings at [SettingsKeys.SELECTED_ACCOUNT].
 *
 * @return The account currently selected, or `null` if the accounts list is empty, or the selection
 * was not found in the list.
 */
@Composable
fun Flow<List<Account>?>.getSelected(): Flow<Account?> {
    val name by settings.getStringState(SettingsKeys.SELECTED_ACCOUNT, "")

    return map { list ->
        if (list.isNullOrEmpty()) null
        else if (name.isBlank()) list.first()
        else list.find { it.name == name }
    }
}
