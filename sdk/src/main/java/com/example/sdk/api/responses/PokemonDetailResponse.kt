package com.example.sdk.api.responses

import com.fasterxml.jackson.annotation.JsonProperty

internal data class PokemonDetailResponse(@JsonProperty("sprites") val sprites: Sprites) {
    data class Sprites(
        @JsonProperty("front_default") val frontDefault: String?,
        @JsonProperty("front_female") val frontFemale: String?
    )
}