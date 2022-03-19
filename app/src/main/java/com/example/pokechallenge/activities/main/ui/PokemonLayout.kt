package com.example.pokechallenge.activities.main.ui

import android.app.Activity
import android.view.Gravity
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pokechallenge.PokemonUISDKInterface
import com.example.pokechallenge.activities.main.logic.MainActivityViewState

@Composable
fun PokemonViewSystemLayout(
    activity: Activity,
    pokemonSDK: PokemonUISDKInterface,
    pokemonDisplayed: MainActivityViewState.PokemonDisplayed
) {
    AndroidView(factory = { context ->
        FrameLayout(context).apply {
            tag = "frame_layout"

            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
                .also { it.gravity = Gravity.CENTER_HORIZONTAL }
        }
    }) { frameLayout ->
        when (pokemonDisplayed) {
            MainActivityViewState.PokemonDisplayed.None -> Unit

            is MainActivityViewState.PokemonDisplayed.Pokemon -> {
                frameLayout.removeAllViews()

                frameLayout.addView(
                    pokemonSDK.createLayoutFrom(
                        activity,
                        pokemonDisplayed.pokemon.image,
                        pokemonDisplayed.pokemon.description
                    )
                )
            }
        }
    }
}