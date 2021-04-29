package me.bristermitten.devdenbot.inject

import io.github.classgraph.ClassGraph

/**
 * @author AlexL
 */
object Classpath {
    private val classGraph by lazy {
        ClassGraph().acceptPackages("me.bristermitten.devdenbot")
            .disableNestedJarScanning()
            .disableModuleScanning()
            .enableClassInfo()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> subtypesOf(type: Class<T>): List<Class<out T>> {
        return classGraph.scan().use {
            it.getSubclasses(type.name).loadClasses()
        } as List<Class<out T>>
    }
}
