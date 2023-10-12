package com.arnyminerz.filamagenta.network.database

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Finds a [SqlTunnelEntry] in the list that has the given [colName].
 *
 * @param colName The name of the column to search for.
 *
 * @return `null` if the column was not found, or the matching [SqlTunnelEntry] otherwise.
 */
fun List<SqlTunnelEntry>.findByColName(colName: String): SqlTunnelEntry? = find { it.metadata.colName == colName }

/**
 * Gets the value of a column named [colName] and of type [String].
 *
 * @param colName The name of the column to search for.
 *
 * @return `null` if the column was not found, or the value stored at that column as a String.
 *
 */
fun List<SqlTunnelEntry>.getString(colName: String): String? = findByColName(colName)?.value

/**
 * Gets the value of a column named [colName] and of type [Long].
 *
 * @param colName The name of the column to search for.
 *
 * @return `null` if the column was not found, or the value stored at that column as a Long.
 *
 */
fun List<SqlTunnelEntry>.getLong(colName: String): Long? = findByColName(colName)?.value?.toLongOrNull()

/**
 * Gets the value of a column named [colName] and of type [LocalDate].
 *
 * @param colName The name of the column to search for.
 *
 * @return `null` if the column was not found, or the value stored at that column as a LocalDate.
 *
 */
fun List<SqlTunnelEntry>.getDate(colName: String): LocalDate? = findByColName(colName)
    ?.value
    ?.substringBeforeLast('Z')?.let(LocalDateTime::parse)
    ?.date
