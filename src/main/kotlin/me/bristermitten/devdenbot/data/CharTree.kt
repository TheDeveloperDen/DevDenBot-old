package me.bristermitten.devdenbot.data

/**
 * A simple implementation of a prefix tree for better parsing
 * Only supports ASCII characters
 * @param <T> The type stored in this CharTree
 * @author Redempt
 */
class CharTree<T> {
    private val root = Node<T>()

    /**
     * Sets a String in this CharTree
     * @param str The String to use as the key
     * @param value The value to store
     */
    operator fun set(str: String, value: T) {
        var node = root
        for (c in str.toCharArray()) {
            node = node.getOrCreateNode(c)
        }
        node.value = value
    }

    /**
     * Gets a value by its key
     * @param str The key
     * @return The value mapped to the key, or null if it is not present
     */
    operator fun get(str: String): T? {
        var node = root
        for (c in str.toCharArray()) {
            node = node.getNode(c) ?: return null
        }
        return node.value
    }

    /**
     * Gets a token forward from the given index in a string
     * @param str The string to search in
     * @param index The starting index to search from
     * @return A pair with the token or null if none was found, and the length parsed
     */
    fun getFrom(str: String, index: Int): Pair<T?, Int> {
        var node = root
        var value: T? = null
        for (i in index until str.length) {
            val child = node.getNode(str[i]) ?: return value to i - index
            node = child
            if (node.value != null) {
                value = node.value
            }
        }
        return value to str.length - index
    }

    private class Node<T> {
        var value: T? = null
        private val children = mutableMapOf<Char, Node<T>>()

        fun getNode(c: Char): Node<T>? {
            return children[c]
        }

        fun getOrCreateNode(c: Char): Node<T> {
            return children.getOrPut(c, ::Node)
        }
    }
}
