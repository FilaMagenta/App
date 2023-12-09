package com.arnyminerz.filamagenta.device

import java.io.File

actual object FSInformation {
    var downloadsDirectory: File? = null

    /**
     * Provides the path of the directory where tickets shall be exported.
     */
    actual fun exportedTicketsDirectory(): com.oldguy.common.io.File {
        val downloads = com.oldguy.common.io.File(downloadsDirectory!!.absolutePath)
        return com.oldguy.common.io.File(downloads, "Tickets")
    }
}
