package com.arnyminerz.filamagenta.sync

/**
 * Works as a template for the specific data sync helpers there might be.
 * See the package's contents to see the different helpers.
 */
fun interface SyncHelper {
    /**
     * Runs the synchronization process.
     *
     * @param arguments The arguments to be used in the synchronization process, if any.
     * Specify them in the implementation's kdoc.
     */
    suspend fun synchronize(vararg arguments: Any?)
}
