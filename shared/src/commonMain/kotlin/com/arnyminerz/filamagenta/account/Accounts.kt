package com.arnyminerz.filamagenta.account

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

/**
 * An object for managing the logged in accounts in the system.
 */
expect class Accounts {
    /**
     * Returns a list of all the accounts currently added to the system's storage.
     */
    fun getAccounts(): List<Account>

    /**
     * Provides a live feed of the account list.
     */
    fun getAccountsLive(): StateFlow<List<Account>>

    /**
     * Adds the given account to the system storage. This method is blocking, which means that
     * once it's completed, calling [getAccounts] should already include [account].
     *
     * @param account The account to store.
     * @param token The token that authorizes the account to use the backend.
     * @param isAdmin Whether the user is an administrator or not.
     */
    fun addAccount(account: Account, token: AccessToken, isAdmin: Boolean)

    /**
     * Clears all the data stored for the given account. This method is blocking, which means that
     * once it's completed, calling [getAccounts] will never include [account].
     *
     * @param account The account to remove from the system.
     */
    fun removeAccount(account: Account)

    /**
     * Updates the stored auth token for the given account.
     * This can be used, for example; for when the token is refreshed, then you want to store the new one using this
     * method.
     *
     * @param account The account owner of the token.
     * @param token The new token.
     * @param expiration The new expiration date.
     */
    fun updateToken(account: Account, token: String, expiration: Instant)

    /**
     * Checks the local storage to see whether the given user is an administrator or not.
     * This has been stored by [addAccount].
     *
     * @param account The account to check whether it's an admin or not.
     *
     * @return `true` if [account] is an administrator, `false` otherwise.
     */
    fun isAdmin(account: Account): Boolean
}
