package me.bristermitten.devdenbot.commands

import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibindingsScanner
import me.bristermitten.devdenbot.inject.Classpath
import me.bristermitten.devdenbot.listener.EventListener
import net.dv8tion.jda.api.hooks.ListenerAdapter

/**
 * @author Alexander Wood (BristerMitten)
 */
class ListenersModule : KotlinModule() {


    override fun configure() {
        val listeners = Classpath.subtypesOf(EventListener::class.java)
        val binder = KotlinMultibinder.newSetBinder<EventListener>(kotlinBinder)

        for (listener in listeners) {
            println("Registered ${listener.name}!")
            binder.addBinding().to(listener)
        }

        install(KotlinMultibindingsScanner.asModule())

        super.configure()
    }
}
