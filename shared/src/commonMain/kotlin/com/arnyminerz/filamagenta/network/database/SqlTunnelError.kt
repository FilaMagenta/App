package com.arnyminerz.filamagenta.network.database

import kotlinx.serialization.Serializable

@Serializable
data class SqlTunnelError(
    val message: String? = null,
    val code: String? = null,
    val number: Int? = null,
    val state: Int? = null,
    val `class`: Int? = null,
    val serverName: String? = null,
    val procName: String? = null,
    val lineNumber: Int? = null
) {
    override fun toString(): String {
        return listOfNotNull(
            message?.let { "Message: $it" },
            code?.let { "Code: $it" },
            number?.let { "Number: $it" },
            state?.let { "State: $it" },
            `class`?.let { "Class: $it" },
            serverName?.let { "Server Name: $it" },
            procName?.let { "Process Name: $it" },
            lineNumber?.let { "Line Number: $it" }
        ).joinToString()
    }
}
