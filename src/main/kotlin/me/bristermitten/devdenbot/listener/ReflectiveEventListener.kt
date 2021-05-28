package me.bristermitten.devdenbot.listener

import me.bristermitten.devdenbot.util.handleEachIn
import me.bristermitten.devdenbot.util.listenFlow
import me.bristermitten.devdenbot.util.scope
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.Event
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Listener

abstract class ReflectiveEventListener : EventListener {
    private fun hasListenerSignature(function: KFunction<*>): Boolean =
        function.hasAnnotation<Listener>()
                && function.parameters.size == 1
                && function.isSuspend
                && function.parameters.first().type.isSubtypeOf(Event::class.createType())

    override fun register(jda: JDA) {
        val functionsToScan = (this::class.declaredMemberFunctions + this::class.declaredMemberExtensionFunctions)
            .asSequence()

        functionsToScan.filter(this::hasListenerSignature)
            .associateWith {
                @Suppress("UNCHECKED_CAST") //hasListenerSignature makes sure this is safe
                it.parameters.first().type as KClass<out Event>
            }.forEach { (function, eventType) ->
                jda.listenFlow(eventType).handleEachIn(scope) { event -> function.callSuspend(event) }
            }
    }

}
