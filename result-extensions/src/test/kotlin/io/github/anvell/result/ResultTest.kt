package io.github.anvell.result

import io.kotest.matchers.shouldBe
import org.junit.Test

private val TestException = Exception()

class ResultTest {

    @Test
    fun `Flat map changes type`() {
        val a = Result.success(1)
        val b = Result.success("result")

        a.flatMap { b } shouldBe b
    }

    @Test
    fun `Error is not affected by flat map`() {
        val a: Result<String> = Result.failure(TestException)
        val b: Result<Int> = Result.success(1)

        a.flatMap { b } shouldBe a
    }

    @Test
    fun `Recovers from error`() {
        val a: Result<String> = Result.failure(TestException)
        val b: Result<Int> = Result.success(1)

        a.or { b } shouldBe b
    }

    @Test
    fun `Successful value is not affected by or`() {
        val a: Result<String> = Result.success("1")
        val b: Result<Int> = Result.failure(TestException)

        a.or { b } shouldBe a
    }
}
