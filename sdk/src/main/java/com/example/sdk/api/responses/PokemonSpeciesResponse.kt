package com.example.sdk.api.responses

import com.fasterxml.jackson.annotation.JsonProperty

internal data class PokemonSpeciesResponse(
    @JsonProperty("flavor_text_entries") val flavorTextEntries: List<FlavorObject>
) {
    data class FlavorObject(
        @JsonProperty("flavor_text") val flavorText: String,
        @JsonProperty("language") val language: Language
    )

    data class Language(@JsonProperty("name") val name: String)
}

internal val PokemonSpeciesResponse.firstEnglishDescriptionOrNull
    get() = flavorTextEntries.firstOrNull { it.language.name == "en" }