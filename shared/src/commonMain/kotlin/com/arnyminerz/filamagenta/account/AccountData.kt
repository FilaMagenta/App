package com.arnyminerz.filamagenta.account

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AccountData(
    val name: String,
    val surname: String,
    val address: String,
    val postalCode: Long,
    val birthday: LocalDate,
    val particularPhone: String?,
    val mobilePhone: String?,
    val workPhone: String?,
    val email: String?
) {
    /**
     * Returns the full name of a person.
     *
     * This property combines the `name` and `surname` properties into a single string, representing the full name of
     * a person.
     *
     * @return the full name of a person.
     */
    val fullName: String get() = "$name $surname"
}
