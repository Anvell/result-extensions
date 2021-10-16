@file:Suppress("unused")

package io.github.anvell.result

/**
 * Returns result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 */
inline fun <R, T : R> Result<T>.flatMap(
    transform: (value: T) -> Result<R>
): Result<R> = when {
    isSuccess -> transform(getOrThrow())
    else -> this
}

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 */
inline fun <R, T : R> Result<T>.or(
    onFailure: (exception: Throwable) -> Result<R>
): Result<R> = when (val exception = exceptionOrNull()) {
    null -> this
    else -> onFailure(exception)
}
