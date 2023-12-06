package com.arnyminerz.filamagenta.storage.external

import android.net.Uri
import com.arnyminerz.filamagenta.android.applicationContext
import com.darkrockstudios.libraries.mpfilepicker.MPFile

actual object ExternalDatabase {
    actual fun import(file: MPFile<Any>): ExternalDatabaseResult {
        val uri = file.platformFile as Uri
        val data = applicationContext.contentResolver
            .openInputStream(uri)!!
            .use { it.readBytes() }
        return ExcelDatabaseProcessor().process(data)
    }
}
