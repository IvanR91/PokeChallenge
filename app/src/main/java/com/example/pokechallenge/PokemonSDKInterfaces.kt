package com.example.pokechallenge

import android.app.Activity
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.sdk.SdkCore
import io.reactivex.rxjava3.core.Single
import java.net.URL

interface PokemonUISDKInterface {
    fun createLayoutFrom(
        activityContext: Activity,
        pokemonSprite: URL,
        description: String
    ): LinearLayoutCompat =
        SdkCore.createLayoutFrom(activityContext, pokemonSprite, description)
}

interface PokemonLogicInterface {
    fun retrieveSpriteOf(pokemon: String): Single<URL> =
        SdkCore.retrieveSpriteOf(pokemon)

    fun retrieveShakespeareDescriptionOf(pokemon: String): Single<String> =
        SdkCore.retrieveShakespeareDescriptionOf(pokemon)
}

class PokemonSDK : PokemonUISDKInterface, PokemonLogicInterface