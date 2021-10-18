package com.example.sdk

import io.reactivex.rxjava3.core.Single
import retrofit2.HttpException
import java.net.SocketTimeoutException

internal fun <T : Any> Single<T>.mapConnectionError(): Single<T> = onErrorResumeNext {
    when (it) {
        is HttpException -> {
            val errorCode = it.code()

            when {
                errorCode == 404 ->
                    Single.error(SdkError.http(errorCode, "Resource not found"))

                errorCode == 429 ->
                    Single.error(SdkError.http(errorCode, "Too many request done"))

                errorCode >= 500 ->
                    Single.error(SdkError.http(errorCode, "Internal server error"))

                else -> Single.error(SdkError.http(errorCode, "Generic error"))
            }
        }

        is SocketTimeoutException -> Single.error(SdkError.timeOut())

        else -> Single.error(SdkError.generic(it))
    }
}