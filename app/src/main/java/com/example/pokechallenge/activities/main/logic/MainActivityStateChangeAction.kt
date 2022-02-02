package com.example.pokechallenge.activities.main.logic

import java.net.URL

sealed class MainActivityStateChangeAction {

    object ErrorOccurred : MainActivityStateChangeAction()

    object SearchExecuted : MainActivityStateChangeAction()

    data class TextModified(val text: String) : MainActivityStateChangeAction()

    data class SearchDone(
        val sprite: URL,
        val description: String
    ) : MainActivityStateChangeAction()
}