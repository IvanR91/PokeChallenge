package com.example.pokechallenge.activities.main.logic

import androidx.lifecycle.ViewModel
import com.example.pokechallenge.activities.main.logic.MainActivityStateChangeAction.*
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.ErrorStatus
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed.None
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val searchPokemonInfoUseCase: SearchPokemonInfoUseCase,
    private val ioScheduler: Scheduler
) : ViewModel() {

    private var currentState = startingState

    fun attachObservables(
        clicks: Observable<Unit>,
        textChanges: Observable<String>
    ): Observable<MainActivityViewState> {
        val modifyTextObservable = textChanges
            .map { TextModified(it.toString()) }

        val clickObservable = clicks
            .filter { currentState.editTextString.isNotBlank() }
            .flatMap {
                searchPokemonInfoUseCase.execute(currentState.editTextString)
                    .subscribeOn(ioScheduler)
                    .toObservable()
                    .map<MainActivityStateChangeAction> { SearchDone(it) }
                    .onErrorResumeNext {
                        Observable.just(ErrorOccurred(it.message ?: "Generic error"))
                    }
                    .startWith(Observable.just(SearchExecuted))
            }

        return Observable.merge(modifyTextObservable, clickObservable)
            .startWith(Observable.just(OnStart))
            .map {
                when (it) {
                    is SearchDone -> currentState.copy(
                        showLoading = false,
                        pokemonDisplayed = Pokemon(it.pokemon),
                        errorStatus = ErrorStatus.None
                    )

                    SearchExecuted -> currentState.copy(
                        showLoading = true,
                        errorStatus = ErrorStatus.None
                    )

                    is TextModified -> currentState.copy(
                        editTextString = it.text,
                        errorStatus = ErrorStatus.None
                    )

                    is ErrorOccurred -> currentState.copy(
                        showLoading = false,
                        errorStatus = ErrorStatus.Show(it.errorText)
                    )

                    OnStart -> currentState.copy(errorStatus = ErrorStatus.None)
                }
            }
            .doOnNext { currentState = it }
    }

    companion object {
        val startingState = MainActivityViewState(
            showLoading = false,
            editTextString = "",
            pokemonDisplayed = None,
            errorStatus = ErrorStatus.None
        )
    }
}