package com.example.pokechallenge.activities.main.logic

import com.example.pokechallenge.PokemonLogicInterface
import io.reactivex.rxjava3.core.Single
import java.net.URL
import javax.inject.Inject

class SearchPokemonInfoUseCase @Inject constructor(private val pokemonSDK: PokemonLogicInterface) {

    fun execute(pokemon: String): Single<Pair<URL, String>> =
        Single.zip(
            pokemonSDK.retrieveSpriteOf(pokemon),
            pokemonSDK.retrieveShakespeareDescriptionOf(pokemon)
        )
        { sprite, description -> sprite to description }
}