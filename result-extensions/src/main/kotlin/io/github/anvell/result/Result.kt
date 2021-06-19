@file:Suppress("unused")

package io.github.anvell.result

inline fun <R, T : R> Result<T>.flatMap(
    transform: (value: T) -> Result<R>
): Result<R> = when {
    isSuccess -> transform(getOrThrow())
    else -> this
}

inline fun <R, T : R> Result<T>.or(
    transform: (exception: Throwable) -> Result<R>
): Result<R> = when (val exception = exceptionOrNull()) {
    null -> this
    else -> transform(exception)
}
