package com.example.windy.di

import com.example.windy.repository.WeatherRepository
import com.example.windy.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
abstract class RepositoryModules {
    @Binds
    abstract fun provideWeatherRepositoryImpl(repository: WeatherRepositoryImpl): WeatherRepository
}
