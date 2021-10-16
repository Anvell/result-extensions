package io.github.anvell.result

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.math.roundToInt

private val TestException = Exception()

class BindingTest {

    @Test
    fun `Expressions are properly evaluated`() {
        val a = Result.success(1)
        val b = Result.success(2)
        val c = Result.success("E")

        binding {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            "$one$two$three"
        } shouldBe Result.success("12E")
    }

    @Test
    fun `Returns on first error expression`() {
        val a = Result.success(1)
        val b: Result<Int> = createError()
        val c: Result<Int> = createError()

        binding {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two + three
        } shouldBe Result.failure(TestException)
    }

    @Test
    fun `Returns on first error expression when different types are used`() {
        val a = Result.success(1)
        val b: Result<Float> = createError()
        val c: Result<Int> = createError()

        binding {
            val one = a.bind()
            val two = b.bind()
            val three = c.bind()

            one + two.roundToInt() + three
        } shouldBe Result.failure(TestException)
    }

    private fun <T> createError(): Result<T> {
        return Result.failure(TestException)
    }
}
