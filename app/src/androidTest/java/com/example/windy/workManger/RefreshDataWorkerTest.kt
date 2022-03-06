package com.example.windy.workManger

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RefreshDataWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testRefreshDataWork() {
        val worker = TestListenableWorkerBuilder<RefreshDataWorker>(context).build()

        // Start the work synchronously
        //  val result = worker.startWork().get()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(Result.success()))
        }

    }
}