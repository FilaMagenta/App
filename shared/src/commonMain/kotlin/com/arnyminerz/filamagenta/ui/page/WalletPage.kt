package com.arnyminerz.filamagenta.ui.page

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.toAccountTransaction
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.database.findByColName
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun WalletPage(account: Account) {
    val transactions by Cache.transactions.collectAsState(null)

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            var idSocio = accounts.getIdSocio(account)
            if (idSocio == null) {
                try {
                    Napier.i("Account doesn't have an stored idSocio. Searching now...")
                    val result = SqlServer.query("SELECT * FROM tbSocios WHERE Dni='${account.name}';")
                    if (result.isNullOrEmpty()) {
                        Napier.e("SQLServer returned a null or empty list.")
                        return@launch
                    }
                    val entries = result[0]
                    require(entries.isNotEmpty()) { "Could not find user in tbSocios." }

                    val idSocioEntry = entries.findByColName("idSocio")
                    checkNotNull(idSocioEntry) { "idSocio to store must not be null." }

                    idSocio = idSocioEntry.value!!.toInt()
                    accounts.setIdSocio(account, idSocio)

                    Napier.i("Updated idSocio for $account: $idSocio")
                } catch (e: SqlTunnelException) {
                    Napier.e("SQLServer returned an error.", throwable = e)
                }
            }
            checkNotNull(idSocio) { "idSocio must not be null." }

            Napier.d("Getting transactions list from server...")
            val result = SqlServer.query("SELECT * FROM tbApuntesSocios WHERE idSocio=$idSocio;")
                ?: return@launch Napier.w { "Account doesn't have any transaction." }
            Napier.d("Got ${result.size} transactions for current account.")
            for (row in result) {
                val transaction = row.toAccountTransaction()
                Cache.insertOrUpdate(transaction)
            }
        }
    }

    Text("There are ${transactions?.size} transactions stored.")
}
