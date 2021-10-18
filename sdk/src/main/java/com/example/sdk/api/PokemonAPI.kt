package com.example.sdk.api

import com.example.sdk.api.responses.PokemonDetailResponse
import com.example.sdk.api.responses.PokemonSpeciesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

internal interface PokemonAPI {
    @GET("pokemon-species/{name}/")
    fun getPokemonSpecies(@Path("name") pokemonName: String): Single<PokemonSpeciesResponse>

    @GET("pokemon/{name}/")
    fun getPokemonDetail(@Path("name") pokemonName: String): Single<PokemonDetailResponse>
}