package com.arnyminerz.filamagenta.device

import kotlinx.io.files.Path

expect object FSInformation {
    /**
     * Provides the path of the directory where tickets shall be exported.
     */
    fun exportedTicketsDirectory(): Path
}
