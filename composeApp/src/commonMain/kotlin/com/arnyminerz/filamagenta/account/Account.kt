package com.arnyminerz.filamagenta.account

data class Account(
    val name: String
) {
    override fun toString(): String = name
}
