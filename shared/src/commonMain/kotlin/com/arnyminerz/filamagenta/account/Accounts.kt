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
    fun getAccountsLive(): StateFlow<List<Account>?>

    /**
     * Adds the given account to the system storage. This method is blocking, which means that
     * once it's completed, calling [getAccounts] should already include [account].
     *
     * @param account The account to store.
     * @param token The token that authorizes the account to use the backend.
     * @param isAdmin Whether the user is an administrator or not.
     * @param email The email associated with the user. Can be fetched later with [getEmail].
     */
    fun addAccount(account: Account, token: AccessToken, isAdmin: Boolean, email: String)

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

    /**
     * Fetches the email associated with the given [account].
     *
     * @return The email stored for the given [account].
     */
    fun getEmail(account: Account): String

    /**
     * Fetches the local accounts storage for the ID of the user in the SQLServer database.
     * Update the value with [setIdSocio].
     *
     * @param account The account to check for.
     *
     * @return The ID of the user in the SQLServer database, or null if none is stored.
     */
    fun getIdSocio(account: Account): Int?

    /**
     * Stores the ID of the user for the SQLServer database in the accounts' storage for the given user.
     * Fetch the value with [getIdSocio].
     *
     * @param account The account to store the ID into.
     * @param idSocio The ID to store.
     */
    fun setIdSocio(account: Account, idSocio: Int)

    /**
     * Fetches the local accounts storage for the ID of the user in WooCommerce.
     * Update the value with [setCustomerId].
     *
     * @param account The account to check for.
     *
     * @return The ID of the user in WooCommerce, or null if none is stored.
     */
    fun getCustomerId(account: Account): Int?

    /**
     * Stores the ID of the user for WooCommerce in the accounts' storage for the given user.
     * Fetch the value with [getCustomerId].
     *
     * @param account The account to store the ID into.
     * @param customerId The ID to store.
     */
    fun setCustomerId(account: Account, customerId: Int)
}
