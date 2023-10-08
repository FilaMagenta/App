package com.arnyminerz.filamagenta.account

import android.accounts.AccountManager
import android.os.Bundle
import kotlinx.datetime.Instant

actual class Accounts(private val am: AccountManager) {
    companion object {
        const val AccountType = "com.arnyminerz.filamagenta.account"
        const val TokenType = "com.arnyminerz.filamagenta.account.token"

        const val UserDataExpiration = "token_expiration"
        const val UserDataRefreshToken = "refresh_token"
    }

    actual fun getAccounts(): List<Account> {
        return am.getAccountsByType(AccountType).map { Account(it.name, it.type) }
    }

    actual fun addAccount(account: Account, token: AccessToken) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        if (!am.addAccountExplicitly(aa, "", Bundle())) {
            throw RuntimeException(
                "Could not add account. Either  the account already exists, the user is locked, or another error has occurred."
            )
        }
        am.setAuthToken(aa, TokenType, token.token)
        am.setUserData(aa, UserDataExpiration, token.expiration.toEpochMilliseconds().toString())
        am.setUserData(aa, UserDataRefreshToken, token.refreshToken)
    }

    actual fun removeAccount(account: Account) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        if (!am.removeAccountExplicitly(aa)) {
            throw RuntimeException(
                "Could not remove account. Either the account does not exist, or another error has occurred."
            )
        }
    }

    actual fun updateToken(account: Account, token: String, expiration: Instant) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        am.setAuthToken(aa, TokenType, token)
        am.setUserData(aa, UserDataExpiration, expiration.toEpochMilliseconds().toString())
    }
}
