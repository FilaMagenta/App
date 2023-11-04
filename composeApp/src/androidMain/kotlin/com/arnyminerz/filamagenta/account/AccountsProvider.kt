package com.arnyminerz.filamagenta.account

import android.accounts.AccountManager
import android.content.Context

actual class AccountsProvider(private val context: Context) {
    actual fun provide(): Accounts {
        val am = AccountManager.get(context)
        return Accounts(am).also { accounts = it }
    }
}
