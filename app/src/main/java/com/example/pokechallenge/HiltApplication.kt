package com.example.pokechallenge

import android.app.Application
import android.util.Log
import com.example.sdk.SdkError
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins

@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler {
            when (it) {

                is UndeliverableException -> when (it.cause) {

                    is SdkError -> Log.w("Error occurred", "Stack -> $it")

                    else -> throw it
                }

                else -> throw it
            }
        }
    }
}