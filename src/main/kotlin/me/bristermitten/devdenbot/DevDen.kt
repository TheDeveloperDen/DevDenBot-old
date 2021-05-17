package me.bristermitten.devdenbot

import com.google.inject.Guice
import com.jagrosh.jdautilities.command.CommandClient
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.misfitlabs.kotlinguice4.getInstance
import io.sentry.Sentry
import kotlinx.serialization.json.Json
import me.bristermitten.devdenbot.commands.CommandsModule
import me.bristermitten.devdenbot.commands.DevDenCommand
import me.bristermitten.devdenbot.events.Events
import me.bristermitten.devdenbot.graphics.GraphicsContext
import me.bristermitten.devdenbot.inject.DevDenModule
import me.bristermitten.devdenbot.leaderboard.Leaderboards
import me.bristermitten.devdenbot.listener.ListenersModule
import me.bristermitten.devdenbot.serialization.DDBConfig
import me.bristermitten.devdenbot.util.log
import me.bristermitten.devdenbot.xp.VoiceChatXPTask
import net.dv8tion.jda.api.JDA
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class DevDen {
    private val log by log()

    private fun load() {
        val config = Json.decodeFromString(DDBConfig.serializer(), javaClass.getResource("/config.json")!!.readText())
            .let {
                it.copy(token = System.getenv("DDB_TOKEN") ?: it.token)
            }

        val loadStatsTime = measureTimeMillis {
            loadDatabase()
            Leaderboards.initializeLeaderboards()
        }

        log.debug { "Loading stats and connecting to database took $loadStatsTime ms." }

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


    private fun loadDatabase() {
        val host = System.getenv("DDB_DB_HOST")
        val db = System.getenv("DDB_DB_NAME")
        val dbUsername = System.getenv("DDB_DB_USERNAME")
        val dbPassword = System.getenv("DDB_DB_PASSWORD")
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://$host/$db"
            driverClassName = "com.mysql.cj.jdbc.Driver"
            username = dbUsername
            password = dbPassword
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Events)
        }
    }

    private fun startTasks(jda: JDA) {
        log.info { "starting tasks..." }
        val timer = Timer()
        timer.schedule(VoiceChatXPTask(jda), 0L, TimeUnit.SECONDS.toMillis(60))
    }
}
