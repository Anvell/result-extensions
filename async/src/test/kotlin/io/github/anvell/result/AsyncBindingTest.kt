package io.github.anvell.result

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.math.roundToInt

private val TestException = Exception()

class AsyncBindingTest {

    @Test
    fun `Suspendable expressions are properly evaluated`() {
        val a = suspend { Result.success(1) }
        val b = suspend { Result.success(2) }

        runBlocking {
            bindingAsync {
                val one = a().bind()
                val two = b().bind()

                one + two
            }.await() shouldBe Result.success(3)
        }
    }

    @Test
    fun `Suspendable and regular expressions are properly evaluated`() {
        val a = Result.success(1)
        val b = suspend { Result.success(2) }
        val c = suspend { Result.success(3) }

        runBlocking {
            bindingAsync {
                val one = a.bind()
                val two = b().bind()
                val three = async { c() }

                one + two + three.bind()
            }.await() shouldBe Result.success(6)
        }
    }

    @Test
    fun `Returns on first error expression`() {
        val a: suspend () -> Result<Int> = suspend { Result.success(1) }
        val b: suspend () -> Result<Int> = suspend { Result.failure(TestException) }
        val c: suspend () -> Result<Int> = suspend { Result.failure(TestException) }

        runBlocking {
            bindingAsync {
                val one = async { a() }
                val two = async { b() }
                val three = async { c() }

                one.bind() + two.bind() + three.bind()
            }.await() shouldBe Result.failure(TestException)
        }
    }

    @Test
    fun `Returns on first error expression when different types are used`() {
        val a: suspend () -> Result<Int> = suspend { Result.success(1) }
        val b: suspend () -> Result<Float> = suspend { Result.failure(TestException) }
        val c: Result<Int> = Result.failure(TestException)

        runBlocking {
            bindingAsync {
                val one = a().bind()
                val two = b().bind()
                val three = c.bind()

                one + two.roundToInt() + three
            }.await() shouldBe Result.failure(TestException)
        }
    }
}
