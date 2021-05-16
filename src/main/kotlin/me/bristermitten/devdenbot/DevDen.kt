package me.bristermitten.devdenbot

import com.google.inject.Guice
import com.jagrosh.jdautilities.command.CommandClient
import dev.misfitlabs.kotlinguice4.getInstance
import io.sentry.Sentry
import kotlinx.serialization.json.Json
import me.bristermitten.devdenbot.commands.CommandsModule
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.data.StatsUsers
import me.bristermitten.devdenbot.graphics.GraphicsContext
import me.bristermitten.devdenbot.inject.DevDenModule
import me.bristermitten.devdenbot.leaderboard.Leaderboards
import me.bristermitten.devdenbot.listener.ListenersModule
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.stats.GlobalStats
import me.bristermitten.devdenbot.util.log
import me.bristermitten.devdenbot.xp.VoiceChatXPTask
import net.dv8tion.jda.api.JDA
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

class DevDen {
    private val log by log()

    private fun load() {
        val config = Json.decodeFromString(DDBConfig.serializer(), javaClass.getResource("/config.json")!!.readText())
            .let {
                it.copy(token = System.getenv("DDB_TOKEN") ?: it.token)
            }

        val loadStatsTime = measureTimeMillis {
            loadStats()
            Leaderboards.initializeLeaderboards()
        }

        log.debug { "Loading stats took $loadStatsTime ms." }

        val injector = Guice.createInjector(DevDenModule(config), CommandsModule(), ListenersModule())

        val jda = injector.getInstance<JDA>()

        val commandClient = injector.getInstance<CommandClient>()
        val commands = injector.getInstance<Set<DevDenCommand>>()

        commands.forEach {
            commandClient.addCommand(it)
            log.debug("Registered ${it.javaClass.name}!")
        }

        jda.awaitReady()
        GraphicsContext.init()
        startTasks(jda)
    }

    fun start() {
        Sentry.init() // This will get the DSN from the SENTRY_DSN environment variable
        try {
            load()
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
        Thread.sleep(1000)
    }

    private fun startTasks(jda: JDA) {
        log.info { "starting tasks..." }
        val timer = Timer()
        timer.schedule(0, TimeUnit.MINUTES.toMillis(5)) {
            saveStats()
        }
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            saveStats()
        })

        timer.schedule(VoiceChatXPTask(jda), 0L, TimeUnit.SECONDS.toMillis(60))
    }

    private val statsFilePath = "/var/data/stats.json"
    private val globalStatsFilePath = "/var/data/globalstats.json"

    private fun loadStats() {
        log.trace("loading stats...")
        val statsFile = File(statsFilePath)
        if (statsFile.exists()) {
            val content = statsFile.readText()
            StatsUsers.loadFrom(content)
            log.trace { "Loading stats from $statsFilePath finished." }
        }

        val globalStatsFile = File(globalStatsFilePath)
        if (globalStatsFile.exists()) {
            GlobalStats.loadFrom(globalStatsFile.readText())
            log.trace { "Loading global stats from $globalStatsFilePath finished." }
        }
    }

    private fun saveStats() {
        log.trace("saving stats...")
        val statsFile = File(statsFilePath)
        val content = StatsUsers.saveToString()
        statsFile.writeText(content)

        val globalStatsFile = File(globalStatsFilePath)
        globalStatsFile.writeText(GlobalStats.saveToString())
    }

}
