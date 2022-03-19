package com.example.pokechallenge.activities.main.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Modifier
import com.example.pokechallenge.PokemonUISDKInterface
import com.example.pokechallenge.activities.main.logic.MainActivityViewModel
import com.example.pokechallenge.activities.main.logic.MainActivityViewState
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    @Inject
    lateinit var pokemonSDK: PokemonUISDKInterface

    private val textChangeSubject = PublishSubject.create<String>()
    private val clickSearchSubject = PublishSubject.create<Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val state = viewModel.attachObservables(clickSearchSubject, textChangeSubject)
                .subscribeAsState(initial = MainActivityViewModel.startingState)

            MainScreen(this, state, textChangeSubject, clickSearchSubject, pokemonSDK)
        }
    }
}

@Composable
fun MainScreen(
    activity: Activity,
    state: State<MainActivityViewState>,
    textChangeSubject: PublishSubject<String>,
    clickSearchSubject: PublishSubject<Unit>,
    pokemonSDK: PokemonUISDKInterface
) {
    MaterialTheme {
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                SearchBar(
                    text = state.value.editTextString,
                    buttonEnable = state.value.isButtonEnabled,
                    isSearching = state.value.showLoading,
                    searchChangeText = textChangeSubject::onNext,
                    clickSearch = { clickSearchSubject.onNext(Unit) }
                )

                PokemonViewSystemLayout(
                    activity,
                    pokemonSDK,
                    state.value.pokemonDisplayed
                )
            }

            SnackBar(scaffoldState = scaffoldState, error = state.value.errorStatus)
        }
    }
}