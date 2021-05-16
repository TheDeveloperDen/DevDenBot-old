package me.bristermitten.devdenbot.data

import me.bristermitten.devdenbot.util.atomic
import java.math.BigInteger
import kotlin.test.*

internal class AtomicBigIntegerTest {

    @Test
    fun `plusAssign adds and sets the variable`() {
        // val works since += mutates x, while ++ reassigns x. Interesting.
        val x = BigInteger.valueOf(5).atomic()
        x += BigInteger.valueOf(10)
        assertEquals(15, x.get().toInt())
    }

    @Test
    fun `plus adds a value`() {
        val x = BigInteger.valueOf(5).atomic()
        val y = x + BigInteger.valueOf(10).atomic()
        assertEquals(5, x.get().toInt())
        assertEquals(15, y.get().toInt())
    }

    @Test
    fun `minusAssign subtracts and sets the variable`() {
        val x = BigInteger.valueOf(5).atomic()
        x -= BigInteger.valueOf(10)
        assertEquals(-5, x.get().toInt())
    }

    @Test
    fun `inc increments and sets the variable`() {
        var x = BigInteger.valueOf(5).atomic()
        val result = x++

        assertEquals(6, result.get().toInt())
        assertEquals(6, x.get().toInt())
    }

    @Test
    fun `dec decrements and sets the variable`() {
        var x = BigInteger.valueOf(5).atomic()
        val result = x--

        assertEquals(4, result.get().toInt())
        assertEquals(4, x.get().toInt())
    }
}