package com.arnyminerz.filamagenta.account

actual class AccountsProvider {
    actual fun provide(): Accounts {
        return Accounts().also { accounts = it }
    }
}
