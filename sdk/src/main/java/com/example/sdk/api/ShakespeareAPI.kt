package com.example.sdk.api

import com.example.sdk.api.requests.ShakespeareTranslateRequest
import com.example.sdk.api.responses.ShakespeareTranslateResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

internal interface ShakespeareAPI {
    @POST("shakespeare.json")
    fun getShakespeareText(@Body request: ShakespeareTranslateRequest): Single<ShakespeareTranslateResponse>
}