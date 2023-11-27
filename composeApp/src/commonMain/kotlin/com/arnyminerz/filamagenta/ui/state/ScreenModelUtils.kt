package com.arnyminerz.filamagenta.ui.state

import cafe.adriel.voyager.core.model.ScreenModel
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.account.getSelected
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import io.github.aakira.napier.Napier

/**
 * Returns the stored WooCommerce's Customer ID for the selected account if there's one stored, or
 * searches in the server for one if there's none stored.
 *
 * @throws NullPointerException If there isn't any account selected.
 * @throws IllegalStateException If the server doesn't return a valid customer id for the account.
 */
suspend fun ScreenModel.getOrFetchCustomerId(): Int {
    val accountsList = accounts.getAccounts()
    val account = accountsList.getSelected()!!

    var customerId = accounts.getCustomerId(account)

    if (customerId == null) {
        try {
            Napier.i("Account doesn't have an stored customerId. Searching now...")

            val customer = WooCommerce.Customers.search(account.name)
            checkNotNull(customer) { "A user that matches \"${account.name}\" was not found in the server." }

            customerId = customer.id
            accounts.setCustomerId(account, customerId)
            Napier.i("Updated customerId for $account: $customerId")
        } catch (e: SqlTunnelException) {
            Napier.e("SQLServer returned an error.", throwable = e)
        }
    }

    checkNotNull(customerId) { "customerId must not be null." }

    return customerId
}
