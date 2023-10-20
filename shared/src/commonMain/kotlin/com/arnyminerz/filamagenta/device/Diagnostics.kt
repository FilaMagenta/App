package com.arnyminerz.filamagenta.device

object Diagnostics {
    var updateUserInformation: ((username: String, email: String) -> Unit)? = null

    var deleteUserInformation: (() -> Unit)? = null
}
