package com.example.sdk.api.requests

import com.fasterxml.jackson.annotation.JsonProperty

internal data class ShakespeareTranslateRequest(@field:JsonProperty("text") val text: String)