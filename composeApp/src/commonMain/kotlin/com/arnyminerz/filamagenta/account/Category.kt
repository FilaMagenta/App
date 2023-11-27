package com.arnyminerz.filamagenta.account

import androidx.compose.ui.graphics.Color
import com.arnyminerz.filamagenta.MR
import dev.icerock.moko.resources.StringResource

@Suppress("MagicNumber")
enum class Category(
    val wooCommerceTermId: Int,
    val databaseId: Long,
    val displayName: StringResource,
    val color: Color = Color(0xFFA7A9AB)
) {
    ACOMPANYANTE(34, -1, MR.strings.category_acompanyante),
    INVITADO(35, -1, MR.strings.category_invitado),
    ALEVIN(47, 1, MR.strings.category_alevin, Color(0xFFD1154D)),
    INFANTIL(42, 2, MR.strings.category_infantil, Color(0xFF5eab16)),
    // 3: Juvenil
    // 4: Situ. esp
    FESTER(33, 5, MR.strings.category_fester, Color(0xFF0b65db)),
    JUBILADO(49, 6, MR.strings.category_jubilado, Color(0xFFa40dbd)),
    COL_PROT(51, 7, MR.strings.category_col_prot, Color(0xFFa40dbd)),
    // 8: Baixa
    // 9: Sit Esp Estudis
    COL_PROT_PACK(57, 10, MR.strings.category_col_prot_pack, Color(0xFFa40dbd)),
    // 11: Jubilat amb Pack
    COL_FEST(50, 12, MR.strings.category_col_fester, Color(0xFFa40dbd)),
    COL_FAMI(52, 13, MR.strings.category_col_fami, Color(0xFFa40dbd));

    companion object {
        /**
         * Obtains the [Category] that has the id [databaseId] for the socios database.
         */
        fun forDatabaseId(databaseId: Long): Category? = entries.find { it.databaseId == databaseId }
    }
}
