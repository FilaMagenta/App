package com.arnyminerz.filamagenta.sync.utils

import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.database.getLong
import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.SocketTimeoutException

object AccountUtils {
    /**
     * Tries getting the IdSocio from [accounts] for the selected [account].
     * If it's still not set, fetches it from the SQL server according to the user's [Account.name]
     *
     * @param account The account to fetch the idSocio from.
     *
     * @throws IllegalStateException If the server doesn't return a valid idSocio for [account].
     * @throws SqlTunnelException If there's an error while doing the SQL request.
     * @throws SocketTimeoutException If the request has timed out.
     */
    suspend fun getOrFetchIdSocio(account: Account): Int {
        var idSocio = accounts.getIdSocio(account)
        if (idSocio == null) {
            try {
                Napier.i("Account doesn't have an stored idSocio. Searching now...")
                val result = SqlServer.query("SELECT idSocio FROM tbSocios WHERE Dni='${account.name}';")
                check(result.isNotEmpty()) { "SQLServer returned a null or empty list." }

                // we only have a query, so fetch that one
                val entries = result[0]
                require(entries.isNotEmpty()) { "Could not find user in tbSocios." }

                // There should be one resulting entry, so take that one. We have already checked that there's one
                val row = entries[0]

                idSocio = row.getLong("idSocio")!!.toInt()
                accounts.setIdSocio(account, idSocio)

                Napier.i("Updated idSocio for $account: $idSocio")
            } catch (e: SqlTunnelException) {
                Napier.e("SQLServer returned an error.", throwable = e)
                throw e
            } catch (e: SocketTimeoutException) {
                Napier.e("Connection timed out while trying to fetch idSocio from server.")
                throw e
            }
        }
        checkNotNull(idSocio) { "idSocio must not be null." }

        return idSocio
    }
}
