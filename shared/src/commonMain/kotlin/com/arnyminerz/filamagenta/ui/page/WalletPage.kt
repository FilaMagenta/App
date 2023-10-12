package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.cache.AccountTransaction
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Cache.collectListAsState
import com.arnyminerz.filamagenta.cache.data.toAccountTransaction
import com.arnyminerz.filamagenta.network.database.SqlServer
import com.arnyminerz.filamagenta.network.database.SqlTunnelEntry
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.network.database.getLong
import com.arnyminerz.filamagenta.ui.theme.ExtendedColors
import com.arnyminerz.filamagenta.utils.euros
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalletPage(account: Account) {
    val transactions by Cache.transactions.collectListAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            isLoading = true

            var idSocio = accounts.getIdSocio(account)
            if (idSocio == null) {
                try {
                    Napier.i("Account doesn't have an stored idSocio. Searching now...")
                    val result = SqlServer.query("SELECT idSocio FROM tbSocios WHERE Dni='${account.name}';")
                    if (result.isEmpty()) {
                        Napier.e("SQLServer returned a null or empty list.")
                        return@launch
                    }
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
                }
            }
            checkNotNull(idSocio) { "idSocio must not be null." }

            Napier.d("Getting transactions list from server...")
            val result = SqlServer.query("SELECT * FROM tbApuntesSocios WHERE idSocio=$idSocio;")[0]
            Cache.synchronizeTransactions(
                result.map(List<SqlTunnelEntry>::toAccountTransaction)
            )
        }.invokeOnCompletion { isLoading = false }
    }

    val income = transactions.filter { it.income }.sumOf { it.units * it.cost }
    val outcome = transactions.filter { !it.income }.sumOf { it.units * it.cost }

    LazyColumn {
        if (isLoading) {
            stickyHeader {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        item {
            BalanceCard(
                income,
                outcome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        items(transactions) { transaction ->
            TransactionCard(
                transaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun BalanceCard(income: Double, outcome: Double, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(MR.strings.wallet_income),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = income.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = ExtendedColors.Positive.color()
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(MR.strings.wallet_balance),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = (income - outcome).toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = ExtendedColors.Neutral.color()
                )
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(MR.strings.wallet_outcome),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = outcome.toString(),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium,
                    color = ExtendedColors.Negative.color()
                )
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: AccountTransaction, modifier: Modifier = Modifier) {
    OutlinedCard(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(top = 8.dp)
        ) {
            Text(
                text = transaction.date.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "#${transaction.id}",
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = transaction.description,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {
            if (transaction.units > 1) {
                Text(
                    text = "${transaction.units} x",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = transaction.cost.euros,
                style = MaterialTheme.typography.titleMedium
            )
            if (transaction.units > 1) {
                Text(
                    text = " = ${(transaction.units * transaction.cost).euros}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
