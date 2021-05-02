package me.bristermitten.devdenbot.serialization

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrettyName(val prettyName: String)
