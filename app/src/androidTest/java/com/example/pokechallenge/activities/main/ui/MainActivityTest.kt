package com.example.pokechallenge.activities.main.ui

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.pokechallenge.*
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
import org.hamcrest.core.IsNot.not
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
            PokemonSDK()

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

    @Test
    fun checkButtonEnableBasedOnText() {
        ActivityScenario.launch(MainActivity::class.java)

        Espresso.onView(withId(R.id.btn_search))
            .check(matches(isDisplayed()))
            .check(matches(isNotEnabled()))

        Espresso.onView(withId(R.id.edit_search))
            .perform(typeText("Abra"))

        Espresso.onView(withId(R.id.btn_search))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        Espresso.onView(withId(R.id.edit_search))
            .perform(replaceText(""))

        Espresso.onView(withId(R.id.btn_search))
            .check(matches(isDisplayed()))
            .check(matches(isNotEnabled()))
    }

    @Test
    fun checkSearchPokemonDisplayImageAndText() {
        `when`(pokemonLogicSDK.retrieveSpriteOf("Abra"))
            .thenReturn(Single.just(URL("https://img.pokemondb.net/artwork/large/ivysaur.jpg")))

        `when`(pokemonLogicSDK.retrieveShakespeareDescriptionOf("Abra"))
            .thenReturn(Single.just("Abra description"))

        ActivityScenario.launch(MainActivity::class.java)

        Espresso.onView(withId(R.id.edit_search))
            .perform(typeText("Abra"))

        Espresso.onView(withId(R.id.btn_search))
            .perform(click())

        Espresso.onView(withId(R.id.scroll_container))
            .check { view, _ ->
                val linearLayout = (view as ViewGroup)[0] as LinearLayoutCompat

                assert(linearLayout.isVisible)
                assert((linearLayout[0] as ImageView).isVisible)
                assert((linearLayout[2] as TextView).isVisible)
                assert((linearLayout[2] as TextView).text == "Abra description")
            }
    }

    @Test
    fun checkSearchPokemonDisplayLoadingAndDismissLoadingAfterReturn() {
        `when`(pokemonLogicSDK.retrieveSpriteOf("Abra"))
            .thenReturn(Single.timer(400, TimeUnit.MILLISECONDS).map { URL("") })

        `when`(pokemonLogicSDK.retrieveShakespeareDescriptionOf("Abra"))
            .thenReturn(Single.just("Abra description"))

        ActivityScenario.launch(MainActivity::class.java)

        Espresso.onView(withId(R.id.edit_search))
            .perform(typeText("Abra"))

        Espresso.onView(withId(R.id.btn_search))
            .perform(click())

        Espresso.onView(withId(R.id.btn_search))
            .check(matches(not(isDisplayed())))

        Espresso.onView(withId(R.id.progress))
            .check(matches(isDisplayed()))

        Thread.sleep(500)

        Espresso.onView(withId(R.id.btn_search))
            .check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.progress))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun checkShowSnackBarError() {
        `when`(pokemonLogicSDK.retrieveSpriteOf("Abra"))
            .thenReturn(Single.error(IOException("Error occurred")))

        `when`(pokemonLogicSDK.retrieveShakespeareDescriptionOf("Abra"))
            .thenReturn(Single.just("Abra description"))

        ActivityScenario.launch(MainActivity::class.java)

        Espresso.onView(withId(R.id.edit_search))
            .perform(typeText("Abra"))

        Espresso.onView(withId(R.id.btn_search))
            .perform(click())

        Espresso.onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(isDisplayed()))
            .check(matches(withText("Error occurred")))
    }
}