package me.bristermitten.devdenbot.pasting

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import me.bristermitten.devdenbot.util.VersionProvider

object HasteClient {

    const val baseUrl = "https://paste.developerden.net/"
    private const val documentsEndpoint = baseUrl + "documents"

    private val client by lazy {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }

    suspend fun postCode(codeBlock: String) =
        client.request<HasteResponse>(documentsEndpoint) {
            method = HttpMethod.Post
            headers {

                append(HttpHeaders.UserAgent, "DevDenBot/${VersionProvider.version}")
            }
            body = codeBlock
        }
            .key
            .let { baseUrl + it }

    @Serializable
    class HasteResponse(val key: String)
}
