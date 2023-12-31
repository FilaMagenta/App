package com.arnyminerz.filamagenta.account

/**
 * Converts the multiplatform account type to Android.
 */
val Account.androidAccount: android.accounts.Account
    get() = android.accounts.Account(name, Accounts.ACCOUNT_TYPE)

/**
 * Converts the Android account to shared multiplatform one.
 */
val android.accounts.Account.commonAccount: Account
    get() = Account(name)
