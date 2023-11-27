package com.arnyminerz.filamagenta.sync

import com.arnyminerz.filamagenta.cache.Cache
import com.arnyminerz.filamagenta.cache.data.toEvent
import com.arnyminerz.filamagenta.network.woo.WooCommerce
import com.arnyminerz.filamagenta.storage.SettingsKeys
import com.arnyminerz.filamagenta.storage.settings
import com.russhwolf.settings.set
import io.github.aakira.napier.Napier
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object EventsSyncHelper: SyncHelper {
    private const val MONTH_INDEX_MAY = 4

    /**
     * Gets the start date of the current working year.
     * This can be used for fetching events only for the desired date range.
     *
     * @return The beginning date of the current working year.
     * Will always be the 1st of May, the thing that changes is the year.
     */
    private fun getWorkingYearStart(): LocalDate {
        // Events will only be fetched for this year. Year is considered until May
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = if (now.monthNumber > MONTH_INDEX_MAY) {
            // If right now is after May, the working year is the current one
            now.year
        } else {
            // If before May, working year is the last one
            now.year - 1
        }
        return LocalDate(year, Month.MAY, 1)
    }

    /**
     * Synchronizes all the events in the current working year into the local cache.
     *
     * @param arguments Does not require any arguments.
     */
    override suspend fun synchronize(vararg arguments: Any?) {
        // Events will only be fetched for this year. Year is considered until May
        val modifiedAfter = getWorkingYearStart()

        Napier.d("Getting products from server after $modifiedAfter...")

        WooCommerce.Products.getProductsAndVariations(modifiedAfter).also { pairs ->
            Napier.i("Got ${pairs.size} products from server. Updating cache...")
            Cache.synchronizeEvents(
                pairs.map { (product, variations) -> product.toEvent(variations) }
            )
        }
        Napier.d("Updating last sync time...")
        settings[SettingsKeys.SYS_EVENTS_LAST_SYNC] = Clock.System.now().toEpochMilliseconds()
    }
}
