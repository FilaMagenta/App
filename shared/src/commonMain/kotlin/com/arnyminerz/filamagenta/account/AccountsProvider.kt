package com.arnyminerz.filamagenta.account

/**
 * Should be initialized by [AccountsProvider]. During runtime can be supposed to not being null.
 */
var accounts: Accounts? = null

expect class AccountsProvider {
    fun provide(): Accounts
}
