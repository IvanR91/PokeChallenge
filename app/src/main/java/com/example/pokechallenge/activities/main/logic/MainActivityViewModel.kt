package com.example.pokechallenge.activities.main.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.pokechallenge.activities.main.logic.MainActivityStateChangeAction.*
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.ErrorStatus
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed.None
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed.Pokemon
import com.jakewharton.rxbinding4.InitialValueObservable
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val searchPokemonInfoUseCase: SearchPokemonInfoUseCase,
    private val ioScheduler: Scheduler
) : ViewModel() {

    private var currentState = MainActivityViewState(
        showLoading = false,
        editTextString = "",
        pokemonDisplayed = None,
        errorStatus = ErrorStatus.None
    )

    fun attachObservables(
        clicks: Observable<Unit>,
        textChanges: Observable<CharSequence>
    ): Observable<MainActivityViewState> {
        val modifyTextObservable = textChanges
            .map { TextModified(it.toString()) }

        val clickObservable = clicks
            .filter { currentState.editTextString.isNotBlank() }
            .flatMap {
                searchPokemonInfoUseCase.execute(currentState.editTextString)
                    .subscribeOn(ioScheduler)
                    .toObservable()
                    .map<MainActivityStateChangeAction> {
                        SearchDone(sprite = it.first, description = it.second)
                    }
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
                        pokemonDisplayed = Pokemon(
                            imageURL = it.sprite,
                            description = it.description
                        )
                    )

                    SearchExecuted -> currentState.copy(showLoading = true)

                    is TextModified -> currentState.copy(editTextString = it.text)

                    is ErrorOccurred -> currentState.copy(
                        showLoading = false,
                        errorStatus = ErrorStatus.Show(it.errorText)
                    )

                    OnStart -> currentState.copy(errorStatus = ErrorStatus.None)
                }
            }
            .doOnNext { currentState = it }
    }
}