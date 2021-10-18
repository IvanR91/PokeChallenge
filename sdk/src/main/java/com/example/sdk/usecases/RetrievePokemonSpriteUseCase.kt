package com.example.sdk.usecases

import com.example.sdk.Dependencies
import com.example.sdk.SdkError
import com.example.sdk.api.PokemonAPI
import com.example.sdk.api.responses.getValidUrlIfPossible
import com.example.sdk.mapConnectionError
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.URL

internal class RetrievePokemonSpriteUseCase(
    private val pokemonClient: PokemonAPI,
    private val scheduler: Scheduler,
    private val urlValidator: (String?) -> URL?
) {
    fun execute(pokemonName: String): Single<URL> =
        pokemonClient.getPokemonDetail(pokemonName.lowercase())
            .mapConnectionError()
            .flatMap { response ->
                response.getValidUrlIfPossible(urlValidator)
                    ?.let { Single.just(it) }
                    ?: Single.error(SdkError.noValidPokemonSpriteFound())
            }
            .subscribeOn(scheduler)

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