package com.arnyminerz.filamagenta.account

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.int
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

@OptIn(ExperimentalSettingsImplementation::class)
actual class Accounts {
    companion object {
        private val accountName = StringIndex("account_%d_name")
        private val accountType = StringIndex("account_%d_type")
        private val accountToken = StringIndex("account_%d_token")
        private val accountTExpiration = StringIndex("account_%d_token_expiration")
        private val accountTRefresh = StringIndex("account_%d_token_refresh")
        private val accountIsAdmin = StringIndex("account_%d_admin")
        private val accountIdSocio = StringIndex("account_%d_id_socio")
    }

    private val settings: Settings = KeychainSettings("accounts")

    private var length: Int by settings.int("accounts_count", 0)

    private val accountsLive = MutableStateFlow(value = getAccounts())

    actual fun getAccounts(): List<Account> {
        if (length <= 0) return emptyList()

        val accounts = arrayListOf<Account>()
        for (c in 0 until length) {
            val name = settings.getStringOrNull(accountName(c)) ?: continue
            accounts.add(
                Account(name)
            )
        }
        return accounts
    }

    actual fun addAccount(account: Account, token: AccessToken, isAdmin: Boolean) {
        settings[accountName(length)] = account.name
        settings[accountToken(length)] = token.token
        settings[accountTExpiration(length)] = token.expiration.toEpochMilliseconds()
        settings[accountTRefresh(length)] = token.refreshToken
        settings[accountIsAdmin(length)] = isAdmin
        length += 1

        accountsLive.value = getAccounts()
    }

    actual fun removeAccount(account: Account) {
        val newIndex = length - 1
        settings.remove(accountName(newIndex))
        settings.remove(accountType(newIndex))
        settings.remove(accountToken(newIndex))
        settings.remove(accountTExpiration(newIndex))
        settings.remove(accountTRefresh(newIndex))
        settings.remove(accountIsAdmin(newIndex))
        length = newIndex

        accountsLive.value = getAccounts()
    }

    /**
     * Updates the stored auth token for the given account. This can be used, for example, for when
     * the token is refreshed, then you want to store the new one using this method.
     *
     * @param account The account owner of the token.
     * @param token The new token.
     * @param expiration The new expiration date.
     */
    actual fun updateToken(
        account: Account,
        token: String,
        expiration: Instant
    ) {
        throw UnsupportedOperationException()
    }

    /**
     * Provides a live feed of the account list.
     */
    actual fun getAccountsLive(): StateFlow<List<Account>> = accountsLive

    /**
     * Checks the local storage to see whether the given user is an administrator or not.
     * This has been stored by [addAccount].
     *
     * @param account The account to check whether it's an admin or not.
     *
     * @return `true` if [account] is an administrator, `false` otherwise.
     */
    actual fun isAdmin(account: Account): Boolean {
        val accountId = getAccounts().indexOf(account)
        return settings.getBoolean(accountIsAdmin(accountId), false)
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
        val accountId = getAccounts().indexOf(account)
        return settings.getIntOrNull(accountIdSocio(accountId))
    }

    /**
     * Stores the ID of the user for the SQLServer database in the accounts' storage for the given user.
     * Fetch the value with [getIdSocio].
     *
     * @param account The account to store the ID into.
     * @param idSocio The ID to store.
     */
    actual fun setIdSocio(account: Account, idSocio: Int) {
        val accountId = getAccounts().indexOf(account)
        settings[accountIdSocio(accountId)] = idSocio
    }
}
