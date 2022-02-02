package com.example.pokechallenge.activities.main.logic

import com.example.pokechallenge.activities.main.logic.MainActivityViewState.ErrorStatus
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.Schedulers.trampoline
import io.reactivex.rxjava3.subjects.PublishSubject
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException
import java.net.URL

class MainActivityViewModelTest : FunSpec({

    val urlExpected = URL("https://google.com")
    val descriptionExpected = "pokemon"
    val exceptionExpected = IOException("Failed")

    val startingState = MainActivityViewState(
        showLoading = false,
        editTextString = "",
        pokemonDisplayed = PokemonDisplayed.None,
        errorStatus = ErrorStatus.None
    )

    val useCase: SearchPokemonInfoUseCase = mock()

    test("Check modify text") {
        val vm = MainActivityViewModel(useCase, trampoline())

        val clickObs = Observable.create<Unit> { }
        val textObs = PublishSubject.create<CharSequence>()

        val observer = TestObserver<MainActivityViewState>()

        vm.attachObservables(
            clickObs,
            textObs
        ).subscribe(observer)

        textObs.onNext("a")
        textObs.onNext("ab")

        observer.assertValues(
            startingState,
            startingState.copy(editTextString = "a"),
            startingState.copy(editTextString = "ab")
        )

        observer.values()[0].isButtonEnabled shouldBe false
        observer.values()[1].isButtonEnabled shouldBe true
    }

    test("Execute click doesn't emit cos of empty text") {
        val vm = MainActivityViewModel(useCase, trampoline())

        val clickObs = PublishSubject.create<Unit>()
        val textObs = PublishSubject.create<CharSequence>()

        val observer = TestObserver<MainActivityViewState>()

        vm.attachObservables(
            clickObs,
            textObs
        ).subscribe(observer)

        clickObs.onNext(Unit)

        observer.assertValues(startingState)
    }

    test("Execute click and UseCase successes") {
        val vm = MainActivityViewModel(useCase, trampoline())

        val clickObs = PublishSubject.create<Unit>()
        val textObs = PublishSubject.create<CharSequence>()

        val observer = TestObserver<MainActivityViewState>()

        whenever(useCase.execute(eq("daoc")))
            .thenReturn(Single.just(urlExpected to descriptionExpected))

        vm.attachObservables(
            clickObs,
            textObs
        ).subscribe(observer)

        textObs.onNext("daoc")
        clickObs.onNext(Unit)

        observer.assertValues(
            startingState,
            startingState.copy(editTextString = "daoc"),
            startingState.copy(editTextString = "daoc", showLoading = true),
            startingState.copy(
                editTextString = "daoc",
                showLoading = false,
                pokemonDisplayed = PokemonDisplayed.Pokemon(
                    imageURL = urlExpected,
                    description = descriptionExpected
                )
            )
        )
    }

    test("Execute click and UseCase fails, re-attach observable clears error") {
        val vm = MainActivityViewModel(useCase, trampoline())

        val clickObs = PublishSubject.create<Unit>()
        val textObs = PublishSubject.create<CharSequence>()

        val observer = TestObserver<MainActivityViewState>()
        val observer2 = TestObserver<MainActivityViewState>()

        whenever(useCase.execute(eq("daoc")))
            .thenReturn(Single.error(exceptionExpected))

        vm.attachObservables(
            clickObs,
            textObs
        ).subscribe(observer)

        textObs.onNext("daoc")
        clickObs.onNext(Unit)

        vm.attachObservables(
            clickObs,
            textObs
        ).subscribe(observer2)

        observer.assertValues(
            startingState,
            startingState.copy(editTextString = "daoc"),
            startingState.copy(editTextString = "daoc", showLoading = true),
            startingState.copy(
                editTextString = "daoc",
                showLoading = false,
                pokemonDisplayed = PokemonDisplayed.None,
                errorStatus = ErrorStatus.Show("Failed")
            )
        )

        observer2.assertValues(
            startingState.copy(
                editTextString = "daoc",
                showLoading = false,
                pokemonDisplayed = PokemonDisplayed.None,
                errorStatus = ErrorStatus.None
            )
        )
    }
})
