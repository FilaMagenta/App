package com.arnyminerz.filamagenta.account

/**
 * Should be initialized by [AccountsProvider]. During runtime can be supposed to not being null.
 */
lateinit var accounts: Accounts

expect class AccountsProvider {
    fun provide(): Accounts
}
