package com.arnyminerz.filamagenta.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.await
import androidx.work.workDataOf
import com.arnyminerz.filamagenta.account.Account
import com.arnyminerz.filamagenta.account.accounts
import com.arnyminerz.filamagenta.network.database.SqlTunnelException
import com.arnyminerz.filamagenta.storage.SettingsKeys.SYS_WORKER_LAST_SYNC
import com.arnyminerz.filamagenta.storage.settings
import com.arnyminerz.filamagenta.sync.EventsSyncHelper
import com.arnyminerz.filamagenta.sync.WalletSyncHelper
import com.arnyminerz.filamagenta.sync.utils.AccountUtils
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import io.ktor.client.network.sockets.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlinx.datetime.Clock

class SyncWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    companion object {
        /**
         * The sync execution interval in hours.
         */
        const val INTERVAL_HOURS = 12L

        /**
         * The time in seconds to wait between the constraints for the work has been met, and the work is run.
         */
        private const val INITIAL_DELAY_SECONDS = 30L

        /**
         * The time in minutes to wait between attempts to run the worker.
         */
        private const val BACKOFF_DELAY_MINUTES = 15L

        /**
         * The tag to be added to the periodic worker.
         */
        private const val PERIODIC_WORKER_TAG = "SyncWorker"

        /**
         * The name to be used for identifying the unique work of the synchronization worker.
         */
        private const val UNIQUE_WORK_NAME = "SyncWorker"

        /**
         * The key of the resulting data that contains the list of accounts that failed during the synchronization
         * process.
         */
        private const val RESULT_FAILED_ACCOUNTS = "failed_accounts"

        /**
         * Schedules the periodic unique work that runs in the background every [INTERVAL_HOURS] hours to keep the
         * local database in sync with the server.
         * If already scheduled, this will do nothing, fetches the already scheduled work.
         *
         * @return The scheduled worker info.
         */
        suspend fun schedule(context: Context): LiveData<WorkInfo> {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(INTERVAL_HOURS, TimeUnit.HOURS)
                .setConstraints(
                    Constraints(
                        requiredNetworkType = NetworkType.CONNECTED
                    )
                )
                .setInitialDelay(INITIAL_DELAY_SECONDS, TimeUnit.SECONDS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, BACKOFF_DELAY_MINUTES, TimeUnit.MINUTES)
                .addTag(PERIODIC_WORKER_TAG)
                .build()

            val manager = WorkManager.getInstance(context)

            manager
                .enqueueUniquePeriodicWork(UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
                .await()

            return manager.getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME).map { it.first() }
        }

        /**
         * If there's a scheduled worker (use [schedule]), this returns its [WorkInfo], otherwise its value will be
         * null until something is scheduled.
         */
        fun getWorkLiveData(context: Context): LiveData<WorkInfo?> =
            WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(UNIQUE_WORK_NAME)
                .map { it.firstOrNull() }
    }

    override suspend fun doWork(): Result {
        Napier.i("Synchronizing data cache with server...")

        val failedAccounts = arrayListOf<Account>()

        // Synchronize the wallets for all the accounts
        accounts.getAccounts()
            .also { Napier.d("Synchronizing wallet for ${it.size} accounts.") }
            .forEach { account: Account ->
                try {
                    val idSocio = AccountUtils.getOrFetchIdSocio(account)

                    WalletSyncHelper.synchronize(idSocio)
                } catch (e: IllegalStateException) {
                    Napier.e("Could not find an idSocio for $account.", e)
                    failedAccounts.add(account)
                } catch (e: SqlTunnelException) {
                    Napier.e("The SQL tunnel returned an exception.", e)
                    failedAccounts.add(account)
                } catch (e: SocketTimeoutException) {
                    Napier.e("The request for fetching the idSocio of $account has timed out.", e)
                    failedAccounts.add(account)
                }
            }

        // Synchronize the events
        Napier.d("Synchronizing events with server...")
        EventsSyncHelper.synchronize()

        // Update the last sync time
        settings[SYS_WORKER_LAST_SYNC] = Clock.System.now().toEpochMilliseconds()

        return Result.success(
            workDataOf(
                RESULT_FAILED_ACCOUNTS to failedAccounts.map { it.name }.toTypedArray()
            )
        )
    }
}
