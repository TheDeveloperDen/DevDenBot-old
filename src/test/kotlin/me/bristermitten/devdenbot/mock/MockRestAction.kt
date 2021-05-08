package me.bristermitten.devdenbot.mock

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.requests.RestAction
import java.util.concurrent.CompletableFuture
import java.util.function.BooleanSupplier
import java.util.function.Consumer

class MockRestAction<T>(val value: T) : RestAction<T> {
    override fun getJDA(): JDA {
        TODO()
    }

    override fun setCheck(checks: BooleanSupplier?): RestAction<T> {
        TODO()
    }

    override fun queue(success: Consumer<in T>?, failure: Consumer<in Throwable>?) {
        success?.accept(value)
    }

    override fun complete(shouldQueue: Boolean): T {
        return value
    }

    override fun submit(shouldQueue: Boolean): CompletableFuture<T> {
        return CompletableFuture.completedFuture(value)
    }
}
