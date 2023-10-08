package com.arnyminerz.filamagenta.account

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
     * Adds the given account to the system storage. This method is blocking, which means that
     * once it's completed, calling [getAccounts] should already include [account].
     *
     * @param account The account to store.
     * @param token The token that authorizes the account to use the backend.
     */
    fun addAccount(account: Account, token: AccessToken)

    /**
     * Clears all the data stored for the given account. This method is blocking, which means that
     * once it's completed, calling [getAccounts] will never include [account].
     *
     * @param account The account to remove from the system.
     */
    fun removeAccount(account: Account)

    /**
     * Updates the stored auth token for the given account. This can be used, for example, for when
     * the token is refreshed, then you want to store the new one using this method.
     *
     * @param account The account owner of the token.
     * @param token The new token.
     * @param expiration The new expiration date.
     */
    fun updateToken(account: Account, token: String, expiration: Instant)
}
