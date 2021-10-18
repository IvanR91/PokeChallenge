package com.example.sdk

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import com.bumptech.glide.Glide
import com.example.sdk.databinding.PokemonLayoutBinding
import com.example.sdk.usecases.RetrievePokemonSpriteUseCase
import com.example.sdk.usecases.RetrieveShakespeareDescriptionUseCase
import io.reactivex.rxjava3.core.Single
import java.net.URL

object SdkCore {
    fun retrieveShakespeareDescriptionOf(pokemon: String): Single<String> =
        RetrieveShakespeareDescriptionUseCase.standard.execute(pokemon)

    fun retrieveSpriteOf(pokemon: String): Single<URL> =
        RetrievePokemonSpriteUseCase.standard.execute(pokemon)

    fun createLayoutFrom(
        activityContext: Activity,
        pokemonSprite: URL,
        description: String
    ): LinearLayoutCompat =
        LayoutInflater.from(activityContext)
            .let { PokemonLayoutBinding.inflate(it) }
            .also { it.pokemonLayoutDescription.text = description }
            .also {
                Glide.with(activityContext)
                    .load(pokemonSprite.toString())
                    .into(it.pokemonLayoutImage)
            }.root
}