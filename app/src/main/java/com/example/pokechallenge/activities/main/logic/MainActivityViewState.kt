package com.example.pokechallenge.activities.main.logic

import java.net.URL

data class MainActivityViewState(
    val showLoading: Boolean,
    val editTextString: String,
    val pokemonDisplayed: PokemonDisplayed
) {
    val isButtonEnabled: Boolean
        get() = editTextString.isNotBlank()

    sealed class PokemonDisplayed {
        object None : PokemonDisplayed()
        data class Pokemon(val imageURL: URL, val description: String) : PokemonDisplayed()
    }
}