package com.example.sdk.usecases

import com.example.sdk.*
import com.example.sdk.api.PokemonAPI
import com.example.sdk.api.ShakespeareAPI
import com.example.sdk.api.requests.ShakespeareTranslateRequest
import com.example.sdk.api.responses.PokemonSpeciesResponse
import com.example.sdk.api.responses.PokemonSpeciesResponse.FlavorObject
import com.example.sdk.api.responses.PokemonSpeciesResponse.Language
import com.example.sdk.api.responses.ShakespeareTranslateResponse
import com.example.sdk.api.responses.ShakespeareTranslateResponse.Contents
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
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

class RetrieveShakespeareDescriptionUseCaseTest : FunSpec({

    val validSpeciesResponse = listOf(
        PokemonSpeciesResponse(listOf(FlavorObject("first", Language("en")))),
        PokemonSpeciesResponse(
            listOf(
                FlavorObject("secondo", Language("it")),
                FlavorObject("zweite", Language("de")),
                FlavorObject("second", Language("en"))
            )
        ),
        PokemonSpeciesResponse(
            listOf(
                FlavorObject("third", Language("en")),
                FlavorObject("terzo", Language("it"))
            )
        )
    )

    val invalidSpeciesResponse = listOf(
        PokemonSpeciesResponse(listOf()),
        PokemonSpeciesResponse(
            listOf(
                FlavorObject("quarto", Language("it")),
                FlavorObject("vierte", Language("de"))
            )
        )
    )

    val possibleShakespeareRequests = listOf(
        ShakespeareTranslateRequest("first"),
        ShakespeareTranslateRequest("second"),
        ShakespeareTranslateRequest("third")
    )

    val shakespeareApiResponse = ShakespeareTranslateResponse(Contents("success"))

    // Errors
    val pokemonApiError = IllegalArgumentException("Pokemon API error")
    val shakespeareApiError = IllegalArgumentException("Shakespeare API error")

    // Generators
    val speciesResponseGenerator = Arb.of(validSpeciesResponse + invalidSpeciesResponse)
    val shakespeareResponseGenerator = Arb.of(
        listOf(
            Single.just(shakespeareApiResponse),
            Single.error(shakespeareApiError)
        )
    )

    test("RetrieveShakespeareDescriptionUseCase Property-Based test") {
        forAll(
            pokemonNameGenerator,
            speciesResponseGenerator,
            shakespeareResponseGenerator
        ) { pokemonName, speciesResponse, shakespeareResponse ->

            // Mocks
            val pokemonClient: PokemonAPI = mock()

            whenever(pokemonClient.getPokemonSpecies(argThat {
                validPokemonList.map { it.lowercase() }.contains(this.lowercase())
            }))
                .thenReturn(Single.just(speciesResponse))

            whenever(pokemonClient.getPokemonSpecies(argThat {
                invalidPokemonList.map { it.lowercase() }.contains(this.lowercase())
            }))
                .thenReturn(Single.error(pokemonApiError))


            val shakespeareClient: ShakespeareAPI = mock()

            whenever(shakespeareClient.getShakespeareText(argThat {
                possibleShakespeareRequests.contains(this)
            }))
                .thenReturn(shakespeareResponse)

            // Observer-test
            val testObserver = TestObserver.create<String>()

            // Test execution
            RetrieveShakespeareDescriptionUseCase(
                pokemonClient,
                shakespeareClient,
                Schedulers.trampoline()
            )
                .execute(pokemonName)
                .subscribe(testObserver)

            // Checks
            when {
                testObserver.values().isEmpty() -> testObserver.assertError {
                    when (it) {
                        is SdkError.Generic -> {
                            invalidPokemonList.contains(pokemonName) ||
                                    shakespeareResponse.test().values().isEmpty()

                        }

                        is SdkError.NoEnglishDescriptionFound ->
                            invalidSpeciesResponse.contains(speciesResponse)

                        else -> fail("Unexpected error, $it")
                    }
                }

                testObserver.values().isNotEmpty() -> testObserver.assertValueCount(1)
                    .assertValue {
                        shakespeareResponse.blockingGet() == shakespeareApiResponse &&
                                it == "success"
                    }

                else -> fail("Unexpected result, ${testObserver.values()}")
            }

            true
        }
    }

    test("RetrieveShakespeareDescriptionUseCase pokemon api ERROR test") {
        forAll(createApiExceptionGenerator<PokemonSpeciesResponse>()) { exception ->

            val client: PokemonAPI = mock()

            whenever(client.getPokemonSpecies(any()))
                .thenReturn(Single.error(exception))


            val shakespeareClient: ShakespeareAPI = mock()

            whenever(shakespeareClient.getShakespeareText(any()))
                .thenReturn(Single.just(shakespeareApiResponse))

            val testObserver = TestObserver.create<String>()

            RetrieveShakespeareDescriptionUseCase(
                client,
                shakespeareClient,
                Schedulers.trampoline()
            )
                .execute("")
                .subscribe(testObserver)

            testObserver.values().shouldBeEmpty()

            testObserver.assertError { errorAssert(it, exception) }

            true
        }
    }

    test("RetrieveShakespeareDescriptionUseCase shakespeare api ERROR test") {
        forAll(createApiExceptionGenerator<ShakespeareTranslateResponse>()) { exception ->

            val client: PokemonAPI = mock()

            whenever(client.getPokemonSpecies(any()))
                .thenReturn(
                    Single.just(
                        PokemonSpeciesResponse(listOf(FlavorObject("first", Language("en"))))
                    )
                )


            val shakespeareClient: ShakespeareAPI = mock()

            whenever(shakespeareClient.getShakespeareText(any()))
                .thenReturn(Single.error(exception))

            val testObserver = TestObserver.create<String>()

            RetrieveShakespeareDescriptionUseCase(
                client,
                shakespeareClient,
                Schedulers.trampoline()
            )
                .execute("")
                .subscribe(testObserver)

            testObserver.values().shouldBeEmpty()

            testObserver.assertError { errorAssert(it, exception) }

            true
        }
    }
})

private fun errorAssert(it: Throwable, exception: Exception) = when (it) {

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
