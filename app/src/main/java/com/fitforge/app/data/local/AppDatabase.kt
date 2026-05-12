package com.fitforge.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fitforge.app.data.local.dao.*
import com.fitforge.app.data.local.entity.*

@Database(
    entities = [
        UserProfileEntity::class,
        SleepLogEntity::class,
        DailyCheckInEntity::class,
        WorkoutSessionEntity::class,
        NutritionLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sleepDao(): SleepDao
    abstract fun checkInDao(): CheckInDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun nutritionDao(): NutritionDao
}
