package net.developerden.devdenbot.inject

import io.github.classgraph.ClassGraph

/**
 * @author AlexL
 */
object Classpath {
    private val classGraph by lazy {
        ClassGraph().acceptPackages("net.developerden.devdenbot")
            .disableNestedJarScanning()
            .disableModuleScanning()
            .enableClassInfo()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> subtypesOf(type: Class<T>): List<Class<out T>> {
        return classGraph.scan().use {
            val types = if (type.isInterface) it.getClassesImplementing(type.name) else it.getSubclasses(type.name)
            types.loadClasses()
        } as List<Class<out T>>
    }
}
