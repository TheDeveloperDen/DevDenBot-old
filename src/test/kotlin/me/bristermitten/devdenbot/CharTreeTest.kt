package me.bristermitten.devdenbot

import me.bristermitten.devdenbot.data.CharTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class CharTreeTest {

    @Test
    fun `test simple tree getting and setting`() {
        val tree = CharTree<Int>()
        tree["hello"] = 3
        tree["world"] = 9
        assertEquals(9, tree["world"])
    }
    @Test
    fun `test getting a value returns null if not present`() {
        val tree = CharTree<Int>()
        tree["hello"] = 3
        assertEquals(null, tree["world"])
    }


    @Test
    fun getFrom() {
        val tree = CharTree<Int>()
        tree["hello"] = 3
        tree["world"] = 9
        print(tree.getFrom("hello", 3))
    }
}
