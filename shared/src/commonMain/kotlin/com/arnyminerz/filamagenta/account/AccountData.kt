package com.arnyminerz.filamagenta.account

data class AccountData(
    val name: String,
    val surname: String
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
