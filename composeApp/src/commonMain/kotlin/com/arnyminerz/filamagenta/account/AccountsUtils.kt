package com.arnyminerz.filamagenta.account

import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings

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
