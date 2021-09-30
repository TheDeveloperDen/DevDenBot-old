package net.developerden.devdenbot.learning

import com.google.common.cache.CacheBuilder
import net.developerden.devdenbot.util.getSuggestion
import net.developerden.devdenbot.util.log
import java.time.Duration

object LearningResourcesCache {
    private val logger by log<LearningResourcesCache>()
    private val cache = CacheBuilder.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(5))
        .build<String, LearningResources>()


    suspend fun get(name: String): LearningResources? = cache.getIfPresent(name)
        ?: LearningResourcesClient.getResource("${name.lowercase()}.json")?.apply(this::update)
        ?: getSuggestion(name, cache.asMap().keys)?.let { cache.getIfPresent(it)!! }

    fun all() = cache.asMap().values.toSet()
    private fun update(languageResources: LearningResources) =
        cache.put(languageResources.name, languageResources)

    suspend fun updateAll() {
        logger.info { "Updating all LanguageResources from remote" }
        LearningResourcesClient.getAll().forEach {
            cache.put(it.name, it)
            logger.info { "Updated LanguageResource ${it.name}" }
        }
    }
}
