package me.bristermitten.devdenbot

import com.google.inject.Guice
import com.jagrosh.jdautilities.command.CommandClient
import kotlinx.serialization.json.Json
import me.bristermitten.devdenbot.commands.CommandsModule
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.commands.ListenersModule
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.inject.DevDenModule
import me.bristermitten.devdenbot.listener.VoiceChatXPTask
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.getInstance
import net.dv8tion.jda.api.JDA
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class DevDen {

    private lateinit var jda: JDA

    fun start() {
        val config = Json.decodeFromString(DDBConfig.serializer(), javaClass.getResource("/config.json")!!.readText())
            .let { it.copy(token = System.getenv("DDB_TOKEN") ?: it.token) }

        loadStats()

        val injector = Guice.createInjector(DevDenModule(config), CommandsModule(), ListenersModule())

        jda = injector.getInstance()


        val commandClient = injector.getInstance<CommandClient>()
        val commands = injector.getInstance<Set<DevDenCommand>>()
        commands.forEach {
            commandClient.addCommand(it)
            println("Registered ${it.javaClass.name}!")
        }

        jda.awaitReady()
        startTasks()
    }

    private fun startTasks() {
        val timer = Timer()
        timer.schedule(0, TimeUnit.MINUTES.toMillis(5)) {
            saveStats()
        }
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            saveStats()
        })

        timer.schedule(VoiceChatXPTask(jda), 0L, TimeUnit.SECONDS.toMillis(60))
    }


    private fun loadStats() {
        val statsFile = File("/var/data/stats.json")
        if (!statsFile.exists()) {
            return
        }
        val content = statsFile.readText()
        StatsUsers.loadFrom(content)
    }

    private fun saveStats() {
        val statsFile = File("/var/data/stats.json")
        val content = StatsUsers.saveToString()
        statsFile.writeText(content)
    }

}
