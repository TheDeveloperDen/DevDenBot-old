package me.bristermitten.devdenbot.listener

import dev.misfitlabs.kotlinguice4.KotlinModule
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibinder
import dev.misfitlabs.kotlinguice4.multibindings.KotlinMultibindingsScanner
import me.bristermitten.devdenbot.inject.Classpath

/**
 * @author Alexander Wood (BristerMitten)
 */
class ListenersModule : KotlinModule() {

    override fun configure() {
        val listeners = Classpath.subtypesOf(EventListener::class.java)
        val binder = KotlinMultibinder.newSetBinder<EventListener>(kotlinBinder)

        for (listener in listeners) {
            binder.addBinding().to(listener)
        }

        install(KotlinMultibindingsScanner.asModule())

        super.configure()
    }
}
