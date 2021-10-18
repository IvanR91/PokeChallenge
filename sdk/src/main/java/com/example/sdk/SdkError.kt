package com.example.sdk

sealed class SdkError(override val message: String?) : Throwable(message) {
    data class Generic(val throwable: Throwable): SdkError("Error occurred ${throwable.message}")
    object NoEnglishDescriptionFound : SdkError("No english description found")
    object NoValidPokemonSpriteFound : SdkError("Pokemon image can't be found")
    object TimeOut : SdkError("Check you internet connection and retry")
    data class Http(val code: Int, val _message: String) :
        SdkError("API error: $_message")

    companion object {
        fun noEnglishDescriptionFound(): SdkError = NoEnglishDescriptionFound
        fun noValidPokemonSpriteFound(): SdkError = NoValidPokemonSpriteFound
        fun http(code: Int, message: String): SdkError = Http(code, message)
        fun timeOut(): SdkError = TimeOut
        fun generic(throwable: Throwable): SdkError = Generic(throwable)
    }
}
