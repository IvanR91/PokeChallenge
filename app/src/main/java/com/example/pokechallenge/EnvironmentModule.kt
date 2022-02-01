package com.example.pokechallenge

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EnvironmentModule {

    @Singleton
    @Provides
    fun providePokemonUISDK(): PokemonUISDKInterface =
        PokemonSDK()

    @Singleton
    @Provides
    fun providePokemonLogicSDK(): PokemonLogicInterface =
        PokemonSDK()

    @Singleton
    @Provides
    fun provideIOScheduler(): Scheduler =
        Schedulers.io()
}