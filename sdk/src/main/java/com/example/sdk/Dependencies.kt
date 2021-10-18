package com.example.sdk

import com.example.sdk.api.PokemonAPI
import com.example.sdk.api.ShakespeareAPI
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.net.URL

internal object Dependencies {

    private val sharedOkHttpClient = OkHttpClient()

    private val jacksonFactory = JacksonConverterFactory.create(
        jacksonObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
        )
    )


    val ioScheduler: Scheduler by lazy { Schedulers.io() }

    val pokemonClient: PokemonAPI =
        Retrofit.Builder()
            .client(sharedOkHttpClient)
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(jacksonFactory)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(PokemonAPI::class.java)

    val shakespeareClient: ShakespeareAPI =
        Retrofit.Builder()
            .client(sharedOkHttpClient)
            .baseUrl("https://api.funtranslations.com/translate/")
            .addConverterFactory(jacksonFactory)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(ShakespeareAPI::class.java)

    val urlValidator: (String?) -> URL? by lazy {
        {
            try {
                URL(it)
            } catch (ex: Exception) {
                null
            }
        }
    }
}
