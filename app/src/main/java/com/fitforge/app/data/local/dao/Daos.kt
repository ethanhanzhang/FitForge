package com.fitforge.app.data.local.dao

import androidx.room.*
import com.fitforge.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProfile(profile: UserProfileEntity)
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_log ORDER BY date DESC LIMIT 30")
    fun getRecent(): Flow<List<SleepLogEntity>>

    @Query("SELECT * FROM sleep_log WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): SleepLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SleepLogEntity)

    @Delete
    suspend fun delete(log: SleepLogEntity)
}

@Dao
interface CheckInDao {
    @Query("SELECT * FROM daily_checkin ORDER BY date DESC LIMIT 30")
    fun getRecent(): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_checkin WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyCheckInEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkIn: DailyCheckInEntity)
}

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_session ORDER BY date DESC LIMIT 50")
    fun getRecent(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT COUNT(*) FROM workout_session WHERE completed = 1")
    suspend fun completedCount(): Int

    @Insert
    suspend fun insert(session: WorkoutSessionEntity): Long

    @Update
    suspend fun update(session: WorkoutSessionEntity)
}

@Dao
interface NutritionDao {
    @Query("SELECT * FROM nutrition_log WHERE date = :date")
    fun getByDate(date: String): Flow<List<NutritionLogEntity>>

    @Query("SELECT * FROM nutrition_log ORDER BY date DESC LIMIT 50")
    fun getRecent(): Flow<List<NutritionLogEntity>>

    @Insert
    suspend fun insert(log: NutritionLogEntity): Long

    @Delete
    suspend fun delete(log: NutritionLogEntity)
}
