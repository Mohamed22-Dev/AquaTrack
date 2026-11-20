package com.example.watertrack.data

import com.example.watertrack.util.Security
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class AuthResult {
    data class Success(val userId: Long) : AuthResult()
    object EmailExists : AuthResult()
    object PhoneExists : AuthResult()
    object InvalidCredentials : AuthResult()
}

class Repository(private val db: AppDatabase) {

    // ---- Auth ----
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        birthDate: Long
    ): AuthResult {
        if (email.isNotEmpty() && db.userDao().getByEmail(email) != null) {
            return AuthResult.EmailExists
        }
        if (phone.isNotEmpty() && db.userDao().getByPhone(phone) != null) {
            return AuthResult.PhoneExists
        }
        val id = db.userDao().insert(
            UserEntity(
                username = "$firstName $lastName".trim(),
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                passwordHash = Security.sha256(password),
                birthDate = birthDate
            )
        )
        return AuthResult.Success(id)
    }

    /** identifier = email ou numero de telephone */
    suspend fun login(identifier: String, password: String): AuthResult {
        val user = if (identifier.contains("@")) {
            db.userDao().getByEmail(identifier)
        } else {
            db.userDao().getByPhone(identifier)
        }
        return if (user != null && user.passwordHash == Security.sha256(password))
            AuthResult.Success(user.id) else AuthResult.InvalidCredentials
    }

    fun observeUser(id: Long) = db.userDao().observe(id)
    suspend fun updateUser(user: UserEntity) = db.userDao().update(user)

    suspend fun updateDailyGoal(userId: Long, goalMl: Int) {
        val user = db.userDao().getById(userId) ?: return
        db.userDao().update(user.copy(dailyGoalMl = goalMl))
    }

    suspend fun setPhoto(userId: Long, path: String) {
        val user = db.userDao().getById(userId) ?: return
        db.userDao().update(user.copy(photoPath = path))
    }

    // ---- Water ----
    suspend fun addWater(userId: Long, amountMl: Int) = db.waterLogDao().insert(
        WaterLogEntity(userId = userId, amountMl = amountMl, timestamp = System.currentTimeMillis())
    )

    suspend fun undoLast(userId: Long) = db.waterLogDao().deleteLast(userId)

    fun todayTotal(userId: Long) =
        db.waterLogDao().totalBetween(userId, startOfDay(0), endOfDay())

    fun weekTotals(userId: Long) =
        db.waterLogDao().dailyTotals(userId, startOfDay(-6))

    // ---- Goals ----
    fun goalHistory(userId: Long) = db.goalDao().history(userId)
    suspend fun saveGoal(goal: GoalEntity) = db.goalDao().upsert(goal)

    suspend fun syncTodayGoal(userId: Long, targetMl: Int, drunkMl: Int) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        db.goalDao().upsert(
            GoalEntity(
                userId = userId, date = date,
                targetMl = targetMl, achieved = drunkMl >= targetMl
            )
        )
    }

    // ---- Helpers ----
    private fun startOfDay(offsetDays: Int): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, offsetDays)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun endOfDay(): Long = startOfDay(1) - 1
}