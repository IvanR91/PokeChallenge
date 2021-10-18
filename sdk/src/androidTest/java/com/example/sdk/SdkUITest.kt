package com.example.sdk

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SdkUITest {

    @Test
    fun createLayoutFromTest() {
        ActivityScenario.launch(TestActivity::class.java)

        onView(withId(R.id.pokemon_layout_description))
            .check(matches(isDisplayed()))
            .check(matches(withText("This is a pokemon description")))

        onView(withId(R.id.pokemon_layout_image))
            .check(matches(isDisplayed()))
    }
}