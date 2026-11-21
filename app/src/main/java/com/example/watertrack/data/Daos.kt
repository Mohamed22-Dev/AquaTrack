package com.example.watertrack.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email AND email != '' LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone AND phone != '' LIMIT 1")
    suspend fun getByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun observe(id: Long): Flow<UserEntity?>

    @Update
    suspend fun update(user: UserEntity)
}

@Dao
interface WaterLogDao {
    @Insert
    suspend fun insert(log: WaterLogEntity): Long

    @Query(
        "DELETE FROM water_logs WHERE id = " +
                "(SELECT id FROM water_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1)"
    )
    suspend fun deleteLast(userId: Long)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM water_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun allLogs(userId: Long): Flow<List<WaterLogEntity>>

    @Query("SELECT COALESCE(SUM(amountMl),0) FROM water_logs WHERE userId = :userId AND timestamp BETWEEN :start AND :end")
    fun totalBetween(userId: Long, start: Long, end: Long): Flow<Int>

    @Query("SELECT * FROM water_logs WHERE userId = :userId AND timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun logsBetween(userId: Long, start: Long, end: Long): Flow<List<WaterLogEntity>>

    @Query(
        """
        SELECT strftime('%Y-%m-%d', timestamp/1000, 'unixepoch', 'localtime') AS day,
               SUM(amountMl) AS total
        FROM water_logs
        WHERE userId = :userId AND timestamp >= :since
        GROUP BY day ORDER BY day
        """
    )
    fun dailyTotals(userId: Long, since: Long): Flow<List<DayTotal>>
}

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY date DESC")
    fun history(userId: Long): Flow<List<GoalEntity>>
}