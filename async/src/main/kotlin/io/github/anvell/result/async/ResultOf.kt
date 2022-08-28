package io.github.anvell.result.async

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Calls the specified function [block] and returns its encapsulated result
 * if invocation was successful,
 * catching any [Throwable] exception that was thrown from the [block]
 * function execution and encapsulating it as a failure.
 *
 * In order to avoid breaking structured concurrency of coroutines
 * [CancellationException] is re-thrown.
 */
inline fun <R> resultOf(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        when (e) {
            is TimeoutCancellationException -> Result.failure(e)
            is CancellationException -> throw e
            else -> Result.failure(e)
        }
    }
}
