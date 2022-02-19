package com.example.windy.workManger

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.windy.database.WeatherDatabase
import com.example.windy.network.WeatherApiFilter
import com.example.windy.repository.WeatherRepository
import com.example.windy.util.SharedPreferenceUtil
import timber.log.Timber


class RefreshDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    // run by default on Dispatchers.Default
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val weatherDatabase = WeatherDatabase.getInstance(applicationContext as Application)
        val repository = WeatherRepository(weatherDatabase)
        val sharedPreferenceUtil = SharedPreferenceUtil(applicationContext)

        return try {
            repository.updateAllWeatherData(
                sharedPreferenceUtil.getLanguage() ?: WeatherApiFilter.ENGLISH.value,
                sharedPreferenceUtil.getTempUnit() ?: WeatherApiFilter.IMPERIAL.value
            )
            Timber.i(" start RefreshDataWorker")
            Result.success()
        } catch (error: Throwable) {
            Timber.i(" fail RefreshDataWorker")
            Result.retry()
        }
    }
}
