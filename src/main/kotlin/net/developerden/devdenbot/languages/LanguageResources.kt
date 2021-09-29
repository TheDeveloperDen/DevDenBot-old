package net.developerden.devdenbot.languages

import kotlinx.serialization.Serializable

@Serializable
data class LanguageResources(
    val name: String,
    val description: String,
    val resources: List<Resource>,
) {
    @Serializable
    data class Resource(
        val name: String,
        val url: String,
        val price: String?,
        val pros: List<String>,
        val cons: List<String>,
    )
}
