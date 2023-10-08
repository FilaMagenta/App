package com.arnyminerz.filamagenta.network.ktorfit

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.HttpResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Call<T>.get(): T = suspendCoroutine { cont ->
    onExecute(object : Callback<T> {
        override fun onError(exception: Throwable) {
            cont.resumeWithException(exception)
        }

        override fun onResponse(call: T, response: HttpResponse) {
            cont.resume(call)
        }
    })
}
