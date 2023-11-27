package com.arnyminerz.filamagenta.account

@Suppress("MagicNumber")
enum class Category(
    val wooCommerceTermId: Int,
    val databaseId: Long
) {
    ACOMPANYANTE(34, -1),
    INVITADO(35, -1),
    ALEVIN(47, 1),
    INFANTIL(42, 2),
    // 3: Juvenil
    // 4: Situ. esp
    FESTER(33, 5),
    JUBILADO(49, 6),
    COL_PROT(51, 7),
    // 8: Baixa
    // 9: Sit Esp Estudis
    COL_PROT_PACK(57, 10),
    // 11: Jubilat amb Pack
    COL_FEST(50, 12),
    COL_FAMI(52, 13);

    companion object {
        /**
         * Obtains the [Category] that has the id [databaseId] for the socios database.
         */
        fun forDatabaseId(databaseId: Long): Category? = entries.find { it.databaseId == databaseId }
    }
}
