package com.arnyminerz.filamagenta.storage.external

import com.darkrockstudios.libraries.mpfilepicker.MPFile

expect object ExternalDatabase {
    fun import(file: MPFile<Any>): ExternalDatabaseResult
}
