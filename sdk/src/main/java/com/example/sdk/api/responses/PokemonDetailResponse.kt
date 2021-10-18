package com.example.sdk.api.responses

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL

internal data class PokemonDetailResponse(@JsonProperty("sprites") val sprites: Sprites) {
    data class Sprites(
        @JsonProperty("front_default") val frontDefault: String?,
        @JsonProperty("front_female") val frontFemale: String?
    )
}

internal fun PokemonDetailResponse.getValidUrlIfPossible(urlValidator: (String?) -> URL?): URL? =
    urlValidator(sprites.frontDefault) ?: urlValidator(sprites.frontFemale)
