package net.developerden.devdenbot.pasting


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.developerden.devdenbot.util.VersionProvider
import java.io.InputStream
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

object HasteClient {

    const val baseUrl = "https://paste.developerden.net/"
    private const val documentsEndpoint = baseUrl + "documents"
    private const val userAgent = "User-Agent"

    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .connectTimeout(Duration.ofSeconds(20))
        .build()

    private val json = Json


    suspend fun postCode(codeBlock: InputStream): String = withContext(Dispatchers.IO) {
        @Suppress("BlockingMethodInNonBlockingContext")
        val url = URL(documentsEndpoint).toURI()

        val req = HttpRequest.newBuilder(url)
            .headers(userAgent, "DevDenBot/${VersionProvider.version}")
            .POST(HttpRequest.BodyPublishers.ofInputStream { codeBlock })
            .build()

        val future = client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
        val body = future.await().body()
        baseUrl + json.decodeFromString<HasteResponse>(body).key
    }

    suspend fun postCode(codeBlock: String) = postCode(codeBlock.byteInputStream())


    @Serializable
    class HasteResponse(val key: String)
}
