package com.arnyminerz.filamagenta.account

import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

actual class Accounts(private val am: AccountManager) {
    companion object {
        const val AccountType = "com.arnyminerz.filamagenta.account"
        const val TokenType = "com.arnyminerz.filamagenta.account.token"

        const val UserDataExpiration = "token_expiration"
        const val UserDataRefreshToken = "refresh_token"
    }

    actual fun getAccounts(): List<Account> {
        return am.getAccountsByType(AccountType).map { Account(it.name) }
    }

    actual fun addAccount(account: Account, token: AccessToken) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        check(am.addAccountExplicitly(aa, "", Bundle())) {
            "Could not add account. Either account already exists, user is locked, or another error has occurred."
        }
        am.setAuthToken(aa, TokenType, token.token)
        am.setUserData(aa, UserDataExpiration, token.expiration.toEpochMilliseconds().toString())
        am.setUserData(aa, UserDataRefreshToken, token.refreshToken)
    }

    actual fun removeAccount(account: Account) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        check(am.removeAccountExplicitly(aa)) {
            "Could not remove account. Either account does not exist, or another error has occurred."
        }
    }

    actual fun updateToken(account: Account, token: String, expiration: Instant) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        am.setAuthToken(aa, TokenType, token)
        am.setUserData(aa, UserDataExpiration, expiration.toEpochMilliseconds().toString())
    }

    private val accountsLive = MutableStateFlow<List<Account>>(value = emptyList())

    private val accountsUpdatedListener = OnAccountsUpdateListener {
        accountsLive.value = it.map(android.accounts.Account::commonAccount)
    }

    /**
     * Provides a live feed of the account list.
     */
    actual fun getAccountsLive(): StateFlow<List<Account>> = accountsLive

    fun startWatchingAccounts(looper: Looper) {
        am.addOnAccountsUpdatedListener(
            accountsUpdatedListener,
            Handler(looper),
            true
        )
    }

    fun stopWatchingAccounts() {
        am.removeOnAccountsUpdatedListener(accountsUpdatedListener)
    }
}
