package com.example.pokechallenge.activities.main.logic

import com.example.pokechallenge.PokemonLogicInterface
import com.example.pokechallenge.models.PokemonModel
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SearchPokemonInfoUseCase @Inject constructor(private val pokemonSDK: PokemonLogicInterface) {

    fun execute(pokemon: String): Single<PokemonModel> =
        Single.zip(
            pokemonSDK.retrieveSpriteOf(pokemon),
            pokemonSDK.retrieveShakespeareDescriptionOf(pokemon)
        )
        { sprite, description -> PokemonModel(sprite, description) }
}