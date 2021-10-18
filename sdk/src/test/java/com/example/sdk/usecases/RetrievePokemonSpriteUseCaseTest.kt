package com.example.sdk.usecases

import com.example.sdk.*
import com.example.sdk.api.PokemonAPI
import com.example.sdk.api.responses.PokemonDetailResponse
import com.example.sdk.api.responses.PokemonDetailResponse.Sprites
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import io.kotest.property.forAll
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.URL

class RetrievePokemonSpriteUseCaseTest : FunSpec({

    val validResponse = listOf(
        PokemonDetailResponse(Sprites("https://mysite.com", "w.site.c")),
        PokemonDetailResponse(Sprites("https://mysite.com", null)),
        PokemonDetailResponse(Sprites(null, "https://www.google.it")),
        PokemonDetailResponse(Sprites("www.malformed.c", "https://www.google.it")),
        PokemonDetailResponse(Sprites("https://mysite.com", "https://www.google.it"))
    )

    val invalidUrlResponse = listOf(
        PokemonDetailResponse(Sprites(null, null)),
        PokemonDetailResponse(Sprites(null, "ww.invalid-url.com")),
        PokemonDetailResponse(Sprites("htp:wwww-yr", "ww.invalid-url.com")),
        PokemonDetailResponse(Sprites("htp:wwww-yr", null))
    )

    // Generators
    val responseGenerator = Arb.of(validResponse + invalidUrlResponse)

    // Errors
    val apiError = IllegalArgumentException("API error")

    test("RetrievePokemonSpriteUseCase Property-Based test") {
        forAll(pokemonNameGenerator, responseGenerator) { pokemonName, response ->

            // Mocks
            val client: PokemonAPI = mock()

            whenever(client.getPokemonDetail(argThat {
                validPokemonList.map { it.lowercase() }.contains(this.lowercase())
            }))
                .thenReturn(Single.just(response))

            whenever(client.getPokemonDetail(argThat {
                invalidPokemonList.map { it.lowercase() }.contains(this.lowercase())
            }))
                .thenReturn(Single.error(apiError))

            // Observer-test
            val testObserver = TestObserver.create<URL>()

            // Test execution
            RetrievePokemonSpriteUseCase(
                client,
                Schedulers.trampoline(),
                Dependencies.urlValidator
            )
                .execute(pokemonName)
                .subscribe(testObserver)

            // Checks
            when {
                testObserver.values().isEmpty() -> testObserver.assertError {
                    when (it) {
                        is SdkError.Generic -> invalidPokemonList.contains(pokemonName)

                        is SdkError.NoValidPokemonSpriteFound ->
                            invalidUrlResponse.contains(response)

                        else -> fail("Unexpected error, $it")
                    }
                }

                testObserver.values().isNotEmpty() -> testObserver.assertValueCount(1)
                    .assertValue {
                        when (it) {
                            URL("https://mysite.com") ->
                                response.sprites.frontDefault == "https://mysite.com"

                            URL("https://www.google.it") ->
                                response.sprites.frontDefault == null ||
                                        response.sprites.frontDefault == "www.malformed.c"

                            else -> fail("Unexpected URL, $it")
                        }
                    }

                else -> fail("Unexpected result, ${testObserver.values()}")
            }

            true
        }
    }

    test("RetrievePokemonSpriteUseCase ERROR test") {
        forAll(createApiExceptionGenerator<PokemonDetailResponse>()) { exception ->

            val client: PokemonAPI = mock()

            whenever(client.getPokemonDetail(any()))
                .thenReturn(Single.error(exception))

            val testObserver = TestObserver.create<URL>()

            RetrievePokemonSpriteUseCase(
                client,
                Schedulers.trampoline(),
                Dependencies.urlValidator
            )
                .execute("")
                .subscribe(testObserver)

            testObserver.assertError {
                when (it) {

                    is SdkError.Http -> {
                        when (it._message) {

                            "Too many request done" -> (exception as HttpException).code() == 429

                            "Internal server error" -> (exception as HttpException).code() == 500

                            "Resource not found" -> (exception as HttpException).code() == 404

                            else -> fail("Unexpected exception, $it")
                        }
                    }

                    is SdkError.Generic -> exception is NullPointerException

                    SdkError.TimeOut -> exception is SocketTimeoutException

                    else -> fail("Unexpected exception, $it")
                }
            }

            true
        }
    }
})
