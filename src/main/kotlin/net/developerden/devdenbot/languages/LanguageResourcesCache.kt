package net.developerden.devdenbot.languages

import com.google.common.cache.CacheBuilder
import net.developerden.devdenbot.util.getSuggestion
import net.developerden.devdenbot.util.log
import java.time.Duration

object LanguageResourcesCache {
    private val logger by log<LanguageResourcesCache>()
    private val cache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(5))
        .build<String, LanguageResources>()


    suspend fun get(name: String): LanguageResources? = cache.getIfPresent(name)
        ?: LanguageResourcesClient.getResource("$name.json")?.apply(this::update)
        ?: getSuggestion(name, cache.asMap().keys)?.let { cache.getIfPresent(it)!! }

    fun all() = cache.asMap().values.toSet()
    private fun update(languageResources: LanguageResources) =
        cache.put(languageResources.name, languageResources)

    suspend fun updateAll() {
        logger.info { "Updating all LanguageResources from remote" }
        LanguageResourcesClient.getAll().forEach {
            cache.put(it.name, it)
            logger.info { "Updated LanguageResource ${it.name}" }
        }
    }
}
