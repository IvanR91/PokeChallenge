package com.example.pokechallenge

import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import io.reactivex.rxjava3.core.Single

fun <T : Any> singleOrErrorGenerator(value: T, error: Throwable): Arb<Single<T>> =
    Arb.of(
        Single.just(value),
        Single.error(error)
    )