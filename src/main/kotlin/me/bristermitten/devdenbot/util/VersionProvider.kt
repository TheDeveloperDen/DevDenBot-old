package me.bristermitten.devdenbot.util

object VersionProvider {
    val version by lazy {
        javaClass.classLoader.getResourceAsStream("version.txt")!!.reader().readText()
    }
}