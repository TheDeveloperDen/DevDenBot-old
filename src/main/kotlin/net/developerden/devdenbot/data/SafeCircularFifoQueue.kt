package net.developerden.devdenbot.data

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.commons.collections4.queue.CircularFifoQueue

class SafeCircularFifoQueue<T>(size: Int) : Iterable<T> {
    private val mutex = Mutex()
    private val queue = CircularFifoQueue<T>(size)


    suspend fun add(element: T) = mutex.withLock {
        queue.add(element)
    }

    fun copy() = runBlocking {
        mutex.withLock {
            queue.toList()
        }
    }

    override fun iterator() = copy().iterator()

}
