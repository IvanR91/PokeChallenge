package com.example.sdk.api.responses

import com.fasterxml.jackson.annotation.JsonProperty

internal data class ShakespeareTranslateResponse(@JsonProperty("contents") val contents: Contents) {

    data class Contents(@JsonProperty("translated") val translated: String)
}
