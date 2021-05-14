package me.bristermitten.devdenbot.util

// could be renamed to provide more info if required
object VersionProvider {
    val version by lazy {
        javaClass.classLoader.getResourceAsStream("version.txt")!!.reader().readText().trim() // trim to remove newline before EOF
    }
}