package com.arnyminerz.filamagenta.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class Authenticator(private val context: Context) : AbstractAccountAuthenticator(context) {
    private val am = AccountManager.get(context)

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        /*
        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra(LoginActivity.EXTRA_ACCOUNT_TYPE, accountType)
            putExtra(LoginActivity.EXTRA_AUTH_TOKEN_TYPE, authTokenType)
            putExtra(LoginActivity.EXTRA_ADDING_NEW_ACCOUNT, true)
            putExtra(LoginActivity.EXTRA_RESPONSE, response)
        }
        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intent)
        }
         */
        throw UnsupportedOperationException()
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle?
    ): Bundle {
        var token = am.peekAuthToken(account, Accounts.TokenType)
        val refreshToken = am.getUserData(account, Accounts.UserDataRefreshToken)
        val tokenExpiration: Instant = am.getUserData(account, Accounts.UserDataExpiration)
            .toLong()
            .let(Instant::fromEpochMilliseconds)
        val now = Clock.System.now()

        if (now > tokenExpiration) {
            // token has expired, refresh it
            // todo - refresh token
            val newToken = "..."
            val newExpiration = Clock.System.now()
            accounts!!.updateToken(account.commonAccount, newToken, newExpiration)
            token = newToken
        }

        if (token.isNotEmpty())
            return Bundle().apply {
                putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                putString(AccountManager.KEY_AUTHTOKEN, token)
            }

        // No token is available, or something has happened, ask to login again.
        // todo - launch login
        val intent = Intent() /*Intent(context, LoginActivity::class.java) */.apply {
            putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            // some extra data if needed such as account name and type
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
