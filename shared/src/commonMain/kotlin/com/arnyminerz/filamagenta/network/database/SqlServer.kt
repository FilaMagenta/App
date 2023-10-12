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
     * @throws SqlTunnelException If the server returns an exception.
     */
    suspend fun query(vararg queries: String): List<List<SqlTunnelEntry>>? {
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
            return body.results
        }
    }
}
