package com.arnyminerz.filamagenta.account

import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.os.Build
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
        const val UserDataAdmin = "is_admin"
        const val UserDataIdSocio = "id_socio"
    }

    private val accountTypeFilter: (android.accounts.Account) -> Boolean = { it.type == AccountType }

    actual fun getAccounts(): List<Account> {
        return am.getAccountsByType(AccountType)
            .filter(accountTypeFilter)
            .map { Account(it.name) }
    }

    actual fun addAccount(account: Account, token: AccessToken, isAdmin: Boolean) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        check(am.addAccountExplicitly(aa, "", Bundle())) {
            "Could not add account. Either account already exists, user is locked, or another error has occurred."
        }
        am.setAuthToken(aa, TokenType, token.token)
        am.setUserData(aa, UserDataExpiration, token.expiration.toEpochMilliseconds().toString())
        am.setUserData(aa, UserDataRefreshToken, token.refreshToken)
        am.setUserData(aa, UserDataAdmin, isAdmin.toString())
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

    private val accountsLive = MutableStateFlow<List<Account>?>(value = null)

    private val accountsUpdatedListener = OnAccountsUpdateListener { accounts ->
        accountsLive.value = accounts
            .filter(accountTypeFilter)
            .map(android.accounts.Account::commonAccount)
    }

    /**
     * Provides a live feed of the account list.
     */
    actual fun getAccountsLive(): StateFlow<List<Account>?> = accountsLive

    fun startWatchingAccounts(looper: Looper) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            am.addOnAccountsUpdatedListener(
                accountsUpdatedListener,
                Handler(looper),
                true,
                arrayOf(AccountType)
            )
        } else {
            am.addOnAccountsUpdatedListener(
                accountsUpdatedListener,
                Handler(looper),
                true
            )
        }
    }

    fun stopWatchingAccounts() {
        am.removeOnAccountsUpdatedListener(accountsUpdatedListener)
    }

    /**
     * Checks the local storage to see whether the given user is an administrator or not.
     * This has been stored by [addAccount].
     *
     * @param account The account to check whether it's an admin or not.
     *
     * @return `true` if [account] is an administrator, `false` otherwise.
     */
    actual fun isAdmin(account: Account): Boolean {
        return am.getUserData(account.androidAccount, UserDataAdmin).toBoolean()
    }

    /**
     * Fetches the local accounts storage for the ID of the user in the SQLServer database.
     * Update the value with [setIdSocio].
     *
     * @param account The account to check for.
     *
     * @return The ID of the user in the SQLServer database, or null if none is stored.
     */
    actual fun getIdSocio(account: Account): Int? {
        return am.getUserData(account.androidAccount, UserDataIdSocio)?.toIntOrNull()
    }

    /**
     * Stores the ID of the user for the SQLServer database in the accounts' storage for the given user.
     * Fetch the value with [getIdSocio].
     *
     * @param account The account to store the ID into.
     * @param idSocio The ID to store.
     */
    actual fun setIdSocio(account: Account, idSocio: Int) {
        am.setUserData(account.androidAccount, UserDataIdSocio, idSocio.toString())
    }
}
