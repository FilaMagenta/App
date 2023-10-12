package com.arnyminerz.filamagenta.cache.data

import com.arnyminerz.filamagenta.cache.AccountTransaction
import com.arnyminerz.filamagenta.network.database.SqlTunnelEntry
import com.arnyminerz.filamagenta.network.database.getDate
import com.arnyminerz.filamagenta.network.database.getLong
import com.arnyminerz.filamagenta.network.database.getString

fun List<SqlTunnelEntry>.toAccountTransaction(): AccountTransaction {
    return AccountTransaction(
        id = getLong("idApunte")!!,
        date = getDate("Fecha")!!,
        description = getString("Concepto")!!,
        units = getLong("Unidades")!!,
        cost = getLong("Precio")!!,
        income = getString("Tipo") == "I",
    )
}
