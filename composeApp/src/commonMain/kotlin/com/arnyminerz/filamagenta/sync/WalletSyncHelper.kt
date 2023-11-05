package com.arnyminerz.filamagenta.sync

import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.toAccountTransaction
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlTunnelEntry
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock

object WalletSyncHelper: SyncHelper {
    /**
     * Synchronizes the transactions for a given socio from the server into the local database.
     *
     * @param arguments 0. `idSocio` the id of the socio to synchronize.
     *
     * @throws NullPointerException If `idSocio` has not been passed in [arguments].
     * @throws ClassCastException If `idSocio` is not an [Int].
     */
    override suspend fun synchronize(vararg arguments: Any?) {
        val idSocio = arguments[0]!! as Int

        Napier.d("Getting transactions list from server...")
        val result = SqlServer.select("tbApuntesSocios", "*", SqlServer.SelectParameter.Where("idSocio", idSocio))[0]
        Cache.synchronizeTransactions(
            result.map(List<SqlTunnelEntry>::toAccountTransaction)
        )
        Napier.d("Updating last sync time...")
        settings[SettingsKeys.SYS_WALLET_LAST_SYNC] = Clock.System.now().toEpochMilliseconds()
    }
}
