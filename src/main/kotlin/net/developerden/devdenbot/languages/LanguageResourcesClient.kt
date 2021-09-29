package net.developerden.devdenbot.languages

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.serialization.Serializable
import net.developerden.devdenbot.util.VersionProvider
import net.developerden.devdenbot.util.scope

object LanguageResourcesClient {
    const val baseUrl = "https://developerden.net/learning-resources/"

    private val client by lazy {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()

            }
        }
    }

    @Serializable
    data class IndexEntry(val name: String, val type: String, val mtime: String, val size: Int)

    suspend fun getAll(): List<LanguageResources> {
        val resources = client.request<List<IndexEntry>>(baseUrl) {
            method = HttpMethod.Get
            headers {
                @Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
                append(HttpHeaders.UserAgent, "DevDenBot/${VersionProvider.version}")
            }
        }

        return resources
            .filterNot { it.name.endsWith("schema.json") }
            .map { scope.async { getResource(it.name) } }
            .awaitAll()
            .filterNotNull()
    }

    suspend fun getResource(name: String): LanguageResources? {
        val resourceUrl = "$baseUrl/${name}" // maybe not the most secure thing in the world, but it works
        return runCatching {
            client.request<LanguageResources>(resourceUrl) {
                method = HttpMethod.Get
                headers {
                    @Suppress("EXPERIMENTAL_API_USAGE_FUTURE_ERROR")
                    append(HttpHeaders.UserAgent, "DevDenBot/${VersionProvider.version}")
                }
            }
        }.getOrNull()
    }
}
