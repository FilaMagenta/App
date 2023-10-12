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
            Napier.d("SQL :: $sql")
        }

        httpClient.post(
            URLBuilder(
                protocol = URLProtocol.HTTPS,
                host = BuildKonfig.SqlTunnelHost,
                pathSegments = listOf("query")
            ).build()
        ) {
            contentType(ContentType.Application.Json)
            setBody(query)
        }.apply {
            val body = body<SqlTunnelResponse>()
            if (!body.successful || status.value < HTTP_OK_MIN || status.value > HTTP_OK_MAX) {
                throw SqlTunnelException(status, body.error?.message)
            }

            val results = body.results?.takeIf { it.isNotEmpty() } ?: return emptyList()
            // "results" has one list for each query, and that list contains all the columns one after the other
            return results.map { columns ->
                // Calling toSet gets rid of duplicates
                val columnNames = columns.map { it.metadata.colName }.toSet()
                // The list of lists to provide
                val resultBuilder = arrayListOf<List<SqlTunnelEntry>>()
                // Each list inside the final result
                val listBuilder = arrayListOf<SqlTunnelEntry>()
                // Will group by rows of columnNames.size columns
                for ((index, column) in columns.withIndex()) {
                    val mod = index % columnNames.size
                    if (mod == 0) {
                        if (listBuilder.isNotEmpty()) {
                            resultBuilder.add(listBuilder)
                        }
                        listBuilder.clear()
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
}
