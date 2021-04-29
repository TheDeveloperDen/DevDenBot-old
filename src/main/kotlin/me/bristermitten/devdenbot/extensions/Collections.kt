package me.bristermitten.devdenbot.extensions

import kotlin.random.Random

fun <T> Collection<T>.randomOr(vararg others: T): T {
    return union(others.toList()).random()
}

fun <T, R> Collection<T>.randomOr(vararg others: R, map: (T) -> R): R {
    val size = size + others.size
    val index = Random.nextInt(size)
    return if (index < this.size) {
        map(elementAt(index))
    } else {
        others[index - this.size]
    }

}
