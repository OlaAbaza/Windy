package com.example.windy.workManger

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.windy.network.WeatherApiFilter
import com.example.windy.repository.WeatherRepository
import com.example.windy.util.SharedPreferenceUtil
import timber.log.Timber
import javax.inject.Inject


class RefreshDataWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    // run by default on Dispatchers.Default
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    override suspend fun doWork(): Result {
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
