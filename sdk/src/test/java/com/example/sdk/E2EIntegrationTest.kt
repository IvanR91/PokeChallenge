package com.example.sdk

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.net.URL

class E2EIntegrationTest : FunSpec({

    test("SdkCore.retrieveSpriteOf E2E fleaky test") {
        val result = SdkCore.retrieveSpriteOf("farfetchd")
            .blockingGet()

        println(result)

        result shouldBe URL("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/83.png")
    }

    test("SdkCore.retrieveShakespeareDescriptionOf E2E fleaky test") {
        val result = SdkCore.retrieveShakespeareDescriptionOf("porygon")
            .blockingGet()

        println(result)

        result shouldBe "A pokémon yond consists entirely of programming code. Capable of moving freely in cyberspace."
    }
})