@file:Suppress("unused", "NOTHING_TO_INLINE")

package io.github.anvell.result.async

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

/**
 * Allows to compose a set of [Result] values in an imperative way
 * using suspendable [bind][ResultCoroutineScope.bind] function.
 *
 * Result is returned as  [Deferred] object.
 */
fun <T> CoroutineScope.bindingAsync(
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend ResultCoroutineScope.() -> T
): Deferred<Result<T>> = async(start = start) {
    with(ResultCoroutineScope(coroutineContext)) {
        try {
            Result.success(block())
        } catch (e: BindingCoroutineException) {
            Result.failure(requireNotNull(error))
        }
    }
}

/**
 * Allows to compose a set of [Result] values in an imperative way
 * using suspendable [bind][ResultCoroutineScope.bind] function.
 */
suspend fun <T> binding(
    block: suspend ResultCoroutineScope.() -> T
): Result<T> = coroutineScope {
    with(ResultCoroutineScope(coroutineContext)) {
        try {
            Result.success(block())
        } catch (e: BindingCoroutineException) {
            Result.failure(requireNotNull(error))
        }
    }
}

class ResultCoroutineScope(
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    val mutex = Mutex()
    var error: Throwable? = null

    suspend inline fun <T> Result<T>.bind(): T = when {
        isSuccess -> getOrThrow()
        else -> mutex.withLock {
            if (error == null) {
                error = exceptionOrNull()
            }
            throw BindingCoroutineException
        }
    }

    suspend inline fun <T> Deferred<Result<T>>.bind(): T {
        return await().bind()
    }
}

@PublishedApi
internal object BindingCoroutineException : Exception()
