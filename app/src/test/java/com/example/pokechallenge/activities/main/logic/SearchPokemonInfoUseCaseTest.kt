package com.example.pokechallenge.activities.main.logic

import com.example.pokechallenge.PokemonLogicInterface
import com.example.pokechallenge.models.PokemonModel
import com.example.pokechallenge.singleOrErrorGenerator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.forAll
import org.junit.jupiter.api.fail
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException
import java.net.URL

class SearchPokemonInfoUseCaseTest : FunSpec({

    val expectedUrl = URL("https://google.com")
    val expectedDescription = "pokemon"

    val expectedUrlException = IOException("url error")
    val expectedDescriptionException = IOException("description error")

    val pokemonSDKMock: PokemonLogicInterface = mock()

    test("SearchPokemonInfoUseCase test") {
        forAll(
            Arb.string(5),
            singleOrErrorGenerator(expectedDescription, expectedDescriptionException),
            singleOrErrorGenerator(expectedUrl, expectedUrlException)
        ) { pokemonName, descriptionResult, spriteResult ->

            whenever(pokemonSDKMock.retrieveShakespeareDescriptionOf(eq(pokemonName)))
                .thenReturn(descriptionResult)

            whenever(pokemonSDKMock.retrieveSpriteOf(eq(pokemonName)))
                .thenReturn(spriteResult)

            val observerResult = SearchPokemonInfoUseCase(pokemonSDKMock)
                .execute(pokemonName)
                .test()

            when (observerResult.values().isEmpty()) {
                true -> observerResult.assertError {
                    when (it) {
                        expectedDescriptionException -> descriptionResult.test()
                            .assertError(expectedDescriptionException)

                        expectedUrlException -> spriteResult.test()
                            .assertError(expectedUrlException)

                        else -> fail("Unexpected error, $it")
                    }

                    true
                }

                false -> observerResult.assertValue {
                    it shouldBe PokemonModel(expectedUrl, expectedDescription)

                    descriptionResult.test().values().size shouldBe 1
                    descriptionResult.test().values()[0] shouldBe expectedDescription

                    spriteResult.test().values().size shouldBe 1
                    spriteResult.test().values()[0] shouldBe expectedUrl

                    true
                }
            }

            true
        }
    }
})
