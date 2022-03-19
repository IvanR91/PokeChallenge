package com.example.pokechallenge.activities.main.ui

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.pokechallenge.activities.main.logic.MainActivityViewState

@Composable
fun SnackBar(scaffoldState: ScaffoldState, error: MainActivityViewState.ErrorStatus) {
    when (error) {
        MainActivityViewState.ErrorStatus.None -> scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
        is MainActivityViewState.ErrorStatus.Show -> LaunchedEffect(key1 = error) {
            scaffoldState.snackbarHostState.showSnackbar(error.message)
        }
    }
}