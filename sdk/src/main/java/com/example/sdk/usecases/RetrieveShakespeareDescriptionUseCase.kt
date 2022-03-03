package com.example.sdk.usecases

import com.example.sdk.Dependencies
import com.example.sdk.SdkError
import com.example.sdk.api.PokemonAPI
import com.example.sdk.api.ShakespeareAPI
import com.example.sdk.api.requests.ShakespeareTranslateRequest
import com.example.sdk.api.responses.PokemonSpeciesResponse
import com.example.sdk.mapConnectionError
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single

internal class RetrieveShakespeareDescriptionUseCase(
    private val pokemonClient: PokemonAPI,
    private val shakespeareClient: ShakespeareAPI,
    private val scheduler: Scheduler
) {
    fun execute(pokemonName: String): Single<String> =
        pokemonClient.getPokemonSpecies(pokemonName.lowercase())
            .mapConnectionError()
            .flatMap { getFirstEnglishDescription(it) }
            .flatMap {
                shakespeareClient.getShakespeareText(ShakespeareTranslateRequest(it))
                    .mapConnectionError()
            }
            .map { it.contents.translated }
            .subscribeOn(scheduler)

    private fun getFirstEnglishDescription(response: PokemonSpeciesResponse): Single<String> =
        response.flavorTextEntries.firstOrNull { it.language.name == "en" }
            ?.let { Single.just(it.flavorText) }
            ?: Single.error(SdkError.noEnglishDescriptionFound())

    companion object {
        val standard by lazy {
            RetrieveShakespeareDescriptionUseCase(
                Dependencies.pokemonClient,
                Dependencies.shakespeareClient,
                Dependencies.ioScheduler
            )
        }
    }
}