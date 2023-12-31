package com.arnyminerz.filamagenta.network.database

import com.arnyminerz.filamagenta.BuildKonfig
import com.arnyminerz.filamagenta.network.httpClient
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType

object SqlServer {
    private const val HTTP_OK_MIN = 200
    private const val HTTP_OK_MAX = 299

    /**
     * Runs an SQL query in the SQLServer.
     *
     * @param queries A list of queries to run.
     *
     * @return A list that contains an element for each query.
     * Inside this element, there's another list that contains the result of the operation.
     * This result is a list of all the rows, and each column value as a list.
     *
     * Therefore, a single `SELECT` query will return a 3-deep list.
     * For example, querying a table with three columns, and two rows:
     * ```
     * [
     *   [
     *     ["value row1 col1", "value row1 col2", "value row1 col3"],
     *     ["value row2 col1", "value row2 col2", "value row2 col3"]
     *   ]
     * ]
     * ```
     *
     * @throws SqlTunnelException If the server returns an exception.
     */
    suspend fun query(vararg queries: String): List<List<List<SqlTunnelEntry>>> {
        val query = SqlQueries(
            server = BuildKonfig.SqlHost,
            username = BuildKonfig.SqlUsername,
            password = BuildKonfig.SqlPassword,
            database = BuildKonfig.SqlDatabase,
            queries = listOf(*queries)
        )

        for (sql in queries) {
            Napier.d("SQL :: $sql", tag = "SQL")
        }

        httpClient.post(
            URLBuilder(
                protocol = URLProtocol.HTTPS,
                host = BuildKonfig.SqlTunnelHost,
                pathSegments = listOf("query")
            ).build()
        ) {
            Napier.v("Setting request content type...")
            contentType(ContentType.Application.Json)
            Napier.v("Setting request body...")
            setBody(query)
            Napier.v("Request completed.")
        }.apply {
            Napier.v("Processing server response...", tag = "SQL")
            val body = body<SqlTunnelResponse>()
            if (!body.successful || status.value < HTTP_OK_MIN || status.value > HTTP_OK_MAX) {
                SqlTunnelException(status, body.error?.toString())
                    .also { Napier.e("Server returned an exception.", throwable = it, tag = "SQL") }
                    .also { throw it }
            }

            Napier.v("Request successful, processing results...", tag = "SQL")
            val results = body.results?.takeIf { it.isNotEmpty() } ?: return emptyList()
            // "results" has one list for each query, and that list contains all the columns one after the other
            return results.map { columns ->
                // Calling toSet gets rid of duplicates
                val columnNames = columns.map { it.metadata.colName }.toSet()
                // The list of lists to provide
                val resultBuilder = mutableListOf<List<SqlTunnelEntry>>()
                // Each list inside the final result
                var listBuilder: MutableList<SqlTunnelEntry> = mutableListOf()
                // Will group by rows of columnNames.size columns
                for ((index, column) in columns.withIndex()) {
                    val mod = index % columnNames.size
                    if (mod == 0) {
                        if (listBuilder.isNotEmpty()) {
                            resultBuilder.add(listBuilder)
                        }
                        listBuilder = mutableListOf()
                    }
                    listBuilder.add(column)
                }

                // Add the last column if not added
                if (listBuilder.isNotEmpty()) {
                    resultBuilder.add(listBuilder)
                }

                resultBuilder
            }
        }
    }

    sealed class SelectParameter(val piece: String) {
        class InnerJoin(
            sourceTable: String,
            modifyColumn: String,
            sourceColumn: String
        ): SelectParameter("INNER JOIN $sourceTable ON $modifyColumn=$sourceColumn")

        class Where(
            column: String,
            value: Any
        ): SelectParameter("WHERE $column=${if (value is String) "\"$value\"" else "$value"}")
    }

    /**
     * Executes a SQL SELECT statement on the specified table with optional parameters.
     *
     * @param table the name of the table to select from
     * @param parameters the optional parameters to include in the query
     *
     * @see query
     */
    suspend fun select(table: String, vararg parameters: Any): List<List<List<SqlTunnelEntry>>> {
        Napier.v("Building select request for table $table...", tag = "SQL")
        val columns = parameters.filterIsInstance<String>()
        val query = "SELECT ${columns.joinToString(", ")} FROM $table"
        val queryParameters = parameters.filterIsInstance<SelectParameter>().joinToString(" ") { it.piece }
        val fullSql = "$query $queryParameters;"
        Napier.v("Performing request...", tag = "SQL")
        return query(fullSql)
    }
}
