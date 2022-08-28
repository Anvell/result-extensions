package io.github.anvell.result.async

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.*
import org.junit.Test

private val TestException = Exception()

class ResultOfTest {

    @Test
    fun `Exception are properly encapsulated`() {
        val a = resultOf { throw IllegalArgumentException() }

        a.isFailure shouldBe true
        a.exceptionOrNull().shouldBeInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `Cancellation exception is re-thrown`() {
        runBlocking {
            val job = launch {
                resultOf { awaitCancellation() }
                throw TestException
            }
            delay(10)
            job.cancelAndJoin()
        }
    }
}
