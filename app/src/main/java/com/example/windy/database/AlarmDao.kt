package com.example.windy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.windy.models.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM Alarms")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("SELECT * FROM Alarms Where id = :id ")
    fun getAlarmById(id: Long): Alarm

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarmObj: Alarm): Long

    @Query("DELETE FROM Alarms WHERE id = :id")
    suspend fun deleteAlarmObj(id: Int)
}