@file:Suppress("unused", "NOTHING_TO_INLINE")

package io.github.anvell.result

/**
 * Allows to compose a set of [Result] values in an imperative way
 * using [bind][ResultBindingScope.bind] function.
 */
inline fun <T> binding(
    crossinline block: ResultBindingScope.() -> T
): Result<T> = with(ResultBindingScope()) {
    try {
        Result.success(block())
    } catch (e: BindingException) {
        Result.failure(error)
    }
}

class ResultBindingScope {
    lateinit var error: Throwable

    inline fun <T> Result<T>.bind(): T = when {
        isSuccess -> getOrThrow()
        else -> {
            error = requireNotNull(exceptionOrNull())
            throw BindingException
        }
    }
}

@PublishedApi
internal object BindingException : Throwable()
