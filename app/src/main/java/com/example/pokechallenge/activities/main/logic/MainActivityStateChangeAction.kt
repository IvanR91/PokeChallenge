package com.example.pokechallenge.activities.main.logic

import com.example.pokechallenge.models.PokemonModel

sealed class MainActivityStateChangeAction {

    object OnStart : MainActivityStateChangeAction()

    object SearchExecuted : MainActivityStateChangeAction()

    data class ErrorOccurred(val errorText: String) : MainActivityStateChangeAction()

    data class TextModified(val text: String) : MainActivityStateChangeAction()

    data class SearchDone(val pokemon: PokemonModel) : MainActivityStateChangeAction()
}