package com.example.windy.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alarms")
data class Alarm(
    @ColumnInfo(name = "event")
    var event: String,
    var Date: String,
    var startTime: String,
    var endTime: String,
    var sound: Boolean,
    var description: String

) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}