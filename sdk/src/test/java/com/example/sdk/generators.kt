package com.example.sdk

import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException

val invalidPokemonList = listOf("Tupac", "daoc")
val validPokemonList = listOf("zubat", "Porygon", "rattata", "Kakuna")

val pokemonNameGenerator = Arb.of(validPokemonList + invalidPokemonList)

fun <T> createApiExceptionGenerator() = Arb.of(
    listOf(
        HttpException(
            Response.error<T>(
                429,
                ResponseBody.create(MediaType.get("application/json; charset=utf-8"), "request")
            )
        ),
        HttpException(
            Response.error<T>(
                404,
                ResponseBody.create(MediaType.get("application/json; charset=utf-8"), "not found")
            )
        ),
        HttpException(
            Response.error<T>(
                500,
                ResponseBody.create(MediaType.get("application/json; charset=utf-8"), "server")
            )
        ),
        SocketTimeoutException(),
        NullPointerException()
    )
)