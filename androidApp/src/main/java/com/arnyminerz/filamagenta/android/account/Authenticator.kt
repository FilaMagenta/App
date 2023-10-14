package com.arnyminerz.filamagenta.android.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.arnyminerz.filamagenta.account.Accounts
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.account.commonAccount
import com.arnyminerz.filamagenta.android.MainActivity
import com.arnyminerz.filamagenta.network.Authorization
import java.time.Instant
import kotlinx.coroutines.runBlocking

class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    private val am = AccountManager.get(context)

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_NEW_ACCOUNT, true)
        }
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intent)
        }
    }

    override fun getAuthToken(
        aaResponse: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle?
    ): Bundle {
        var token = am.peekAuthToken(account, Accounts.TokenType)
        val refreshToken = am.getUserData(account, Accounts.UserDataRefreshToken)
        val tokenExpiration: Instant = am.getUserData(account, Accounts.UserDataExpiration)
            .toLong()
            .let(Instant::ofEpochMilli)
        val now = Instant.now()

        if (now > tokenExpiration) {
            // token has expired, refresh it
            val response = runBlocking { Authorization.refreshToken(refreshToken) }
            accounts.updateToken(account.commonAccount, response.accessToken, response.expiration)
            token = response.accessToken
        }

        if (token.isNotEmpty())
            return Bundle().apply {
                putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                putString(AccountManager.KEY_AUTHTOKEN, token)
            }

        // No token is available, or something has happened, ask to login again.
        // todo - account should be removed, or handle somehow the re-login
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, aaResponse)
            // some extra data if needed such as account name and type
            putExtra(MainActivity.EXTRA_NEW_ACCOUNT, true)
        }
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intent)
        }
    }

    override fun getAuthTokenLabel(authTokenType: String): String {
        throw UnsupportedOperationException()
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<out String>
    ): Bundle {
        throw UnsupportedOperationException()
    }

    override fun isCredentialsUpdateSuggested(
        response: AccountAuthenticatorResponse,
        account: Account,
        statusToken: String?
    ): Bundle {
        return super.isCredentialsUpdateSuggested(response, account, statusToken)
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        throw UnsupportedOperationException()
    }
}
