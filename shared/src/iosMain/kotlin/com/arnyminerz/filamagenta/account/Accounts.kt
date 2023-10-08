package com.arnyminerz.filamagenta.account

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.int
import com.russhwolf.settings.set
import kotlinx.datetime.Instant

@OptIn(ExperimentalSettingsImplementation::class)
actual class Accounts {
    companion object {
        private val accountName = StringIndex("account_%d_name")
        private val accountType = StringIndex("account_%d_type")
        private val accountToken = StringIndex("account_%d_token")
        private val accountTExpiration = StringIndex("account_%d_token_expiration")
        private val accountTRefresh = StringIndex("account_%d_token_refresh")
    }

    private val settings: Settings = KeychainSettings("accounts")

    private var length: Int by settings.int("accounts_count", 0)

    actual fun getAccounts(): List<Account> {
        if (length <= 0) return emptyList()

        val accounts = arrayListOf<Account>()
        for (c in 0 until length) {
            val name = settings.getStringOrNull("account_${c}_name") ?: continue
            val type = settings.getStringOrNull("account_${c}_type") ?: continue
            accounts.add(
                Account(name, type)
            )
        }
        return accounts
    }

    actual fun addAccount(account: Account, token: AccessToken) {
        settings[accountName(length)] = account.name
        settings[accountType(length)] = account.type
        settings[accountToken(length)] = token.token
        settings[accountTExpiration(length)] = token.expiration.toEpochMilliseconds()
        settings[accountTRefresh(length)] = token.refreshToken
        length += 1
    }

    actual fun removeAccount(account: Account) {
        val newIndex = length - 1
        settings.remove(accountName(newIndex))
        settings.remove(accountType(newIndex))
        settings.remove(accountToken(newIndex))
        settings.remove(accountTExpiration(newIndex))
        settings.remove(accountTRefresh(newIndex))
        length = newIndex
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
}
