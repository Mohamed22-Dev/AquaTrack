package com.example.watertrack.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val passwordHash: String,
    val birthDate: Long,
    val photoPath: String? = null,
    val dailyGoalMl: Int = 2000,
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val amountMl: Int,
    val timestamp: Long
)

@Entity(tableName = "goals", indices = [Index(value = ["userId", "date"], unique = true)])
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val date: String,        // yyyy-MM-dd
    val targetMl: Int,
    val achieved: Boolean = false
)

data class DayTotal(val day: String, val total: Int)