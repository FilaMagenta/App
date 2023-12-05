package com.arnyminerz.filamagenta.device

import kotlinx.io.files.Path
import java.io.File

actual object FSInformation {
    var downloadsDirectory: File? = null

    /**
     * Provides the path of the directory where tickets shall be exported.
     */
    actual fun exportedTicketsDirectory(): Path {
        val downloads = Path(downloadsDirectory!!.absolutePath)
        return Path(downloads, "Tickets")
    }
}
