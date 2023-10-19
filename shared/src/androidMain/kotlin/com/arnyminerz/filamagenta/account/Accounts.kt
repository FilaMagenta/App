package com.arnyminerz.filamagenta.account

import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString

@Suppress("TooManyFunctions")
actual class Accounts(private val am: AccountManager) {
    companion object {
        const val ACCOUNT_TYPE = "com.arnyminerz.filamagenta.account"
        const val TOKEN_TYPE = "com.arnyminerz.filamagenta.account.token"

        const val USER_DATA_EXPIRATION = "token_expiration"
        const val USER_DATA_REFRESH_TOKEN = "refresh_token"
        const val USER_DATA_IS_ADMIN = "is_admin"
        const val USER_DATA_EMAIL = "email"
        const val USER_DATA_ID_SOCIO = "id_socio"
        const val USER_DATA_CUSTOMER_ID = "customer_id"
        const val USER_DATA_DATA = "data"
    }

    private val accountTypeFilter: (android.accounts.Account) -> Boolean = { it.type == ACCOUNT_TYPE }

    actual fun getAccounts(): List<Account> {
        return am.getAccountsByType(ACCOUNT_TYPE)
            .filter(accountTypeFilter)
            .map { Account(it.name) }
    }

    actual fun addAccount(account: Account, token: AccessToken, isAdmin: Boolean, email: String) {
        /** The equivalent of [account] for Android. */
        val aa = account.androidAccount
        check(am.addAccountExplicitly(aa, "", Bundle())) {
            "Could not add account. Either account already exists, user is locked, or another error has occurred."
        }
        am.setAuthToken(aa, TOKEN_TYPE, token.token)
        am.setUserData(aa, USER_DATA_EXPIRATION, token.expiration.toEpochMilliseconds().toString())
        am.setUserData(aa, USER_DATA_REFRESH_TOKEN, token.refreshToken)
        am.setUserData(aa, USER_DATA_IS_ADMIN, isAdmin.toString())
        am.setUserData(aa, USER_DATA_EMAIL, email)
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
        am.setAuthToken(aa, TOKEN_TYPE, token)
        am.setUserData(aa, USER_DATA_EXPIRATION, expiration.toEpochMilliseconds().toString())
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
                arrayOf(ACCOUNT_TYPE)
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
        return am.getUserData(account.androidAccount, USER_DATA_IS_ADMIN).toBoolean()
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
        return am.getUserData(account.androidAccount, USER_DATA_ID_SOCIO)?.toIntOrNull()
    }

    /**
     * Stores the ID of the user for the SQLServer database in the accounts' storage for the given user.
     * Fetch the value with [getIdSocio].
     *
     * @param account The account to store the ID into.
     * @param idSocio The ID to store.
     */
    actual fun setIdSocio(account: Account, idSocio: Int) {
        am.setUserData(account.androidAccount, USER_DATA_ID_SOCIO, idSocio.toString())
    }

    /**
     * Fetches the local accounts storage for the ID of the user in WooCommerce.
     * Update the value with [setCustomerId].
     *
     * @param account The account to check for.
     *
     * @return The ID of the user in WooCommerce, or null if none is stored.
     */
    actual fun getCustomerId(account: Account): Int? {
        return am.getUserData(account.androidAccount, USER_DATA_CUSTOMER_ID)?.toIntOrNull()
    }

    /**
     * Stores the ID of the user for WooCommerce in the accounts' storage for the given user.
     * Fetch the value with [getCustomerId].
     *
     * @param account The account to store the ID into.
     * @param customerId The ID to store.
     */
    actual fun setCustomerId(account: Account, customerId: Int) {
        am.setUserData(account.androidAccount, USER_DATA_CUSTOMER_ID, customerId.toString())
    }

    /**
     * Fetches the email associated with the given [account].
     *
     * @return The email stored for the given [account].
     */
    actual fun getEmail(account: Account): String {
        return am.getUserData(account.androidAccount, USER_DATA_EMAIL)!!
    }

    /**
     * Retrieves the account data for the specified account.
     *
     * @param account The account for which to retrieve the account data.
     * @return The account data for the specified account, or null if no account data is available.
     */
    actual fun getAccountData(account: Account): AccountData? {
        val dataString = am.getUserData(account.androidAccount, USER_DATA_DATA) ?: return null
        return DefaultJson.decodeFromString<AccountData>(dataString)
    }

    /**
     * Sets the account data for the given account.
     *
     * @param account The account for which the data will be set.
     * @param data The account data to set.
     */
    actual fun setAccountData(account: Account, data: AccountData) {
        am.setUserData(account.androidAccount, USER_DATA_DATA, DefaultJson.encodeToString(data))
    }
}
