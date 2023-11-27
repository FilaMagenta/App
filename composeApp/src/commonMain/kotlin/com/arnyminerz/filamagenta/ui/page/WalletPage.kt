package com.arnyminerz.filamagenta.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arnyminerz.filamagenta.MR
import com.arnyminerz.filamagenta.cache.AccountTransaction
import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.Cache.collectListAsState
import com.arnyminerz.filamagenta.storage.SettingsKeys.SYS_WALLET_LAST_SYNC
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.ui.state.MainScreenModel
import com.arnyminerz.filamagenta.ui.theme.ExtendedColors
import com.arnyminerz.filamagenta.utils.euros
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until

/**
 * Every how many hours should the wallet be refreshed automatically.
 */
private const val SyncWalletEveryHours = 12

@Composable
fun WalletPage(viewModel: MainScreenModel) {
    val transactions by Cache.transactions.collectListAsState()

    DisposableEffect(transactions) {
        val lastSync = settings.getLongOrNull(SYS_WALLET_LAST_SYNC)?.let(Instant::fromEpochMilliseconds)
        val now = Clock.System.now()

        val coroutine = if (
            // Sync if there aren't any transactions
            transactions.isEmpty() ||
            // Or lastSync is null
            // Or time since last sync is greater than SyncWalletEveryHours
            lastSync?.let { it.until(now, DateTimeUnit.HOUR) >= SyncWalletEveryHours } != false
        ) {
            viewModel.refreshWallet()
        } else {
            null
        }

        onDispose { coroutine?.cancel() }
    }

    val income = transactions.filter { it.income }.sumOf { it.units * it.cost }
    val outcome = transactions.filter { !it.income }.sumOf { it.units * it.cost }

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
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
                    text = income.euros,
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
                    text = (income - outcome).euros,
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
                    text = outcome.euros,
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
            val textColor = if (transaction.income) ExtendedColors.Positive.color() else ExtendedColors.Negative.color()

            ProvideTextStyle(
                MaterialTheme.typography.titleMedium.copy(color = textColor)
            ) {
                if (transaction.units > 1) {
                    Text("${transaction.units} x ")
                }
                Text(transaction.cost.euros)
                if (transaction.units > 1) {
                    Text(" = ${(transaction.units * transaction.cost).euros}")
                }
            }
        }
    }
}
