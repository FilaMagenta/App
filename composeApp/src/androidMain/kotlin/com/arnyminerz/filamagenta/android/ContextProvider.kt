package com.arnyminerz.filamagenta.android

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * The context of the application, to be used when needed.
 */
internal lateinit var applicationContext: Context
    private set

/**
 * A dummy content provider that doesn't support any operation, just initializes [applicationContext].
 */
internal class ContextProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        val context = context
        if (context != null) {
            applicationContext = context.applicationContext
        } else {
            error("Context cannot be null")
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        error("Not allowed.")
    }

    override fun getType(uri: Uri): String? {
        error("Not allowed.")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        error("Not allowed.")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        error("Not allowed.")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        error("Not allowed.")
    }
}
