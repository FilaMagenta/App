package com.arnyminerz.filamagenta.device

import com.oldguy.common.io.File

expect object FSInformation {
    /**
     * Provides the path of the directory where tickets shall be exported.
     */
    fun exportedTicketsDirectory(): File
}
