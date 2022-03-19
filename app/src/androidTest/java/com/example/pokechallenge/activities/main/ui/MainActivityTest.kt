package com.example.pokechallenge.activities.main.ui

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.pokechallenge.EnvironmentModule
import com.example.pokechallenge.PokemonLogicInterface
import com.example.pokechallenge.PokemonSDK
import com.example.pokechallenge.PokemonUISDKInterface
import com.example.pokechallenge.activities.main.ui.MainActivityTest.TestSingletonComponent.pokemonLogicSDK
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@UninstallModules(EnvironmentModule::class)
@HiltAndroidTest
class MainActivityTest {

    @Module
    @InstallIn(SingletonComponent::class)
    object TestSingletonComponent {

        val pokemonLogicSDK: PokemonLogicInterface = mock(PokemonLogicInterface::class.java)

        @Singleton
        @Provides
        fun providePokemonUISDK(): PokemonUISDKInterface =
            PokemonSDK

        @Singleton
        @Provides
        fun providePokemonLogicSDK(): PokemonLogicInterface =
            pokemonLogicSDK

        @Singleton
        @Provides
        fun provideIOScheduler(): Scheduler =
            Schedulers.trampoline()
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun checkButtonEnableBasedOnText() {
        ActivityScenario.launch(MainActivity::class.java)

        composeTestRule.onNodeWithText("SEARCH")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeTestRule.onNodeWithText("")
            .performTextInput("Abra")

        composeTestRule.onNodeWithText("SEARCH")
            .assertIsDisplayed()
            .assertIsEnabled()

        composeTestRule.onNodeWithText("Abra")
            .performTextClearance()

        composeTestRule.onNodeWithText("SEARCH")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun checkSearchPokemonDisplayImageAndText() {
        `when`(pokemonLogicSDK.retrieveSpriteOf("Ivysaur"))
            .thenReturn(Single.just(URL("https://img.pokemondb.net/artwork/large/ivysaur.jpg")))

        `when`(pokemonLogicSDK.retrieveShakespeareDescriptionOf("Ivysaur"))
            .thenReturn(Single.just("Ivysaur description"))

        ActivityScenario.launch(MainActivity::class.java)

        composeTestRule.onNodeWithText("")
            .performTextInput("Ivysaur")

        composeTestRule.onNodeWithText("SEARCH")
            .performClick()

        Espresso.onView(withTagValue(Matchers.`is`("frame_layout")))
            .check { view, _ ->
                val linearLayout = (view as ViewGroup)[0] as LinearLayoutCompat

                assert(linearLayout.isVisible)
                assert((linearLayout[0] as ImageView).isVisible)
                assert((linearLayout[2] as TextView).isVisible)
                assert((linearLayout[2] as TextView).text == "Ivysaur description")
            }
    }

    @Test
    fun checkSearchPokemonDisplayLoadingAndDismissLoadingAfterReturn() {
        `when`(pokemonLogicSDK.retrieveSpriteOf("Abra"))
            .thenReturn(Single.timer(1, TimeUnit.SECONDS).map { URL("https://img.pokemondb.net") })

        `when`(pokemonLogicSDK.retrieveShakespeareDescriptionOf("Abra"))
            .thenReturn(Single.just("Abra description"))

        ActivityScenario.launch(MainActivity::class.java)

        composeTestRule.onNodeWithText("")
            .performTextInput("Abra")

        composeTestRule.onNodeWithText("SEARCH")
            .performClick()

        Thread.sleep(500)

        composeTestRule.onNodeWithText("SEARCH")
            .assertDoesNotExist()

        val rangeInfo = ProgressBarRangeInfo(
            current = 0F,
            range = 0F..0F,
            steps = 0
        )

        composeTestRule.onNode(hasProgressBarRangeInfo(rangeInfo))
            .assertIsDisplayed()

        Thread.sleep(1000)

        composeTestRule.onNodeWithText("SEARCH")
            .assertIsDisplayed()
            .assertIsEnabled()

        composeTestRule.onNode(hasProgressBarRangeInfo(rangeInfo))
            .assertDoesNotExist()
    }

    @Test
    fun checkShowSnackBarError() {
        `when`(pokemonLogicSDK.retrieveSpriteOf("Abra"))
            .thenReturn(Single.error(IOException("Error occurred")))

        `when`(pokemonLogicSDK.retrieveShakespeareDescriptionOf("Abra"))
            .thenReturn(Single.just("Abra description"))

        ActivityScenario.launch(MainActivity::class.java)

        composeTestRule.onNodeWithText("")
            .performTextInput("Abra")

        composeTestRule.onNodeWithText("SEARCH")
            .performClick()

        Thread.sleep(50)

        composeTestRule.onNodeWithText("Error occurred")
            .assertIsDisplayed()
    }
}