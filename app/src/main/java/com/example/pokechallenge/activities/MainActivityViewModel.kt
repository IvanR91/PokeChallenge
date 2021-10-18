package com.example.pokechallenge.activities

import androidx.lifecycle.ViewModel
import com.example.pokechallenge.activities.MainActivityViewModel.UIAction.SearchClick
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val searchPokemonInfoUseCase: SearchPokemonInfoUseCase,
    ioScheduler: Scheduler
) : ViewModel() {

    private val subjectUI = BehaviorSubject.create<MainActivityViewState>()
    val observableUI: Observable<MainActivityViewState> = subjectUI

    private val subjectError = PublishSubject.create<String>()
    val observableError: Observable<String> = subjectError

    private val actionSubject = PublishSubject.create<UIAction>()

    private var actionDisposable: Disposable? = null

    init {
        actionDisposable = actionSubject.subscribeOn(ioScheduler)
            .flatMap {
                when (it) {
                    is SearchClick ->
                        searchPokemonInfoUseCase.execute(it.text)
                            .map { (sprite, description) ->
                                currentState.copy(
                                    searchState = SearchState.Loaded(
                                        it.text,
                                        sprite,
                                        description
                                    ),
                                    showLoading = false
                                )
                            }
                            .toObservable()
                            .doOnError { throwable -> subjectError.onNext(throwable.message) }
                            .onErrorResumeNext { Observable.just(currentState.copy(showLoading = false)) }
                            .startWithItem(
                                currentState.copy(
                                    showLoading = true,
                                    searchState = SearchState.Clear
                                )
                            )
                }
            }
            .subscribe {
                currentState = it
                subjectUI.onNext(it)
            }
    }

    private var currentState = MainActivityViewState(
        showLoading = false,
        searchState = SearchState.Clear
    )

    fun clickSearchButton(text: String?) {
        text?.let { actionSubject.onNext(SearchClick(it)) }
    }

    data class MainActivityViewState(
        val showLoading: Boolean,
        val searchState: SearchState
    )

    sealed class SearchState {
        object Clear : SearchState()

        data class Loaded(
            val pokemonName: String,
            val sprite: URL,
            val description: String
        ) : SearchState()
    }

    sealed class UIAction {
        data class SearchClick(val text: String) : UIAction()
    }

    override fun onCleared() {
        actionDisposable?.dispose()
        super.onCleared()
    }
}