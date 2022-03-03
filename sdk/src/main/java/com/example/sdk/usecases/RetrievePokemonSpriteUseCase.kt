package com.example.sdk.usecases

import com.example.sdk.Dependencies
import com.example.sdk.SdkError
import com.example.sdk.api.PokemonAPI
import com.example.sdk.api.responses.PokemonDetailResponse
import com.example.sdk.mapConnectionError
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.net.URL

internal class RetrievePokemonSpriteUseCase(
    private val pokemonClient: PokemonAPI,
    private val scheduler: Scheduler,
    private val urlValidator: (String?) -> Single<URL>
) {
    fun execute(pokemonName: String): Single<URL> =
        pokemonClient.getPokemonDetail(pokemonName.lowercase())
            .mapConnectionError()
            .flatMap { response ->
                getValidUrlIfPossible(response)
                    .onErrorResumeNext { Single.error(SdkError.noValidPokemonSpriteFound()) }
            }
            .subscribeOn(scheduler)

    private fun getValidUrlIfPossible(response: PokemonDetailResponse): Single<URL> =
        urlValidator(response.sprites.frontDefault)
            .onErrorResumeNext { urlValidator(response.sprites.frontFemale) }

    companion object {
        val standard by lazy {
            RetrievePokemonSpriteUseCase(
                Dependencies.pokemonClient,
                Dependencies.ioScheduler,
                Dependencies.urlValidator
            )
        }
    }
}