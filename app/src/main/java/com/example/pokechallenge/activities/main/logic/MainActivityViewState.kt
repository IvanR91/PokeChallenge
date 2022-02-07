package com.example.pokechallenge.activities.main.logic

import com.example.pokechallenge.models.PokemonModel

data class MainActivityViewState(
    val showLoading: Boolean,
    val editTextString: String,
    val pokemonDisplayed: PokemonDisplayed,
    val errorStatus: ErrorStatus
) {
    val isButtonEnabled: Boolean
        get() = editTextString.isNotBlank()

    sealed class PokemonDisplayed {
        object None : PokemonDisplayed()
        data class Pokemon(val pokemon: PokemonModel) : PokemonDisplayed()
    }

    sealed class ErrorStatus {
        object None : ErrorStatus()
        data class Show(val message: String) : ErrorStatus()
    }
}