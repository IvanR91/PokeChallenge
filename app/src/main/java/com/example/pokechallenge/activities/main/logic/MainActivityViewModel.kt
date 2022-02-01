package com.example.pokechallenge.activities.main.logic

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.pokechallenge.activities.main.logic.MainActivityStateChangeAction.*
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

    private val subjectError = PublishSubject.create<String>()
    val observableError: Observable<String> = subjectError

    private var currentState = MainActivityViewState(
        showLoading = false,
        editTextString = "",
        pokemonDisplayed = None
    )

    fun attachObservables(
        clicks: Observable<Unit>,
        textChanges: InitialValueObservable<CharSequence>
    ): Observable<MainActivityViewState> {
        val modifyTextObservable = textChanges
            .skipInitialValue()
            .map { TextModified(it.toString()) }

        val clickObservable = clicks
            .filter { currentState.editTextString.isNotBlank() }
            .flatMap {
                searchPokemonInfoUseCase.execute(currentState.editTextString)
                    .subscribeOn(ioScheduler)
                    .toObservable()
                    .doOnError { throwable -> subjectError.onNext(throwable.message) }
                    .map<MainActivityStateChangeAction> {
                        SearchDone(sprite = it.first, description = it.second)
                    }
                    .onErrorResumeNext { Observable.just(ErrorOccurred) }
                    .startWith(Observable.just(SearchExecuted))
            }

        return Observable.merge(modifyTextObservable, clickObservable)
            .map {
                Log.d("flow", "handling new action $it")
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

                    ErrorOccurred -> currentState.copy(showLoading = false)
                }
            }
            .doOnNext { currentState = it }
    }
}