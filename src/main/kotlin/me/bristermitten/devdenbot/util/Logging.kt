package me.bristermitten.devdenbot.util

import mu.KLogger
import mu.KotlinLogging
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.full.companionObject

/**
 * @author AlexL
 */

class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, KLogger> {
    override fun getValue(thisRef: R, property: KProperty<*>): KLogger =
        KotlinLogging.logger(getClassForLogging(thisRef.javaClass).simpleName)
}

fun <T : Any> log() = LoggerDelegate<T>()

private fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}
