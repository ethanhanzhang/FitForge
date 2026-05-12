package com.fitforge.app.di

import android.content.Context
import androidx.room.Room
import com.fitforge.app.data.local.AppDatabase
import com.fitforge.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "fitforge.db").build()

    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
    @Provides fun provideSleepDao(db: AppDatabase): SleepDao = db.sleepDao()
    @Provides fun provideCheckInDao(db: AppDatabase): CheckInDao = db.checkInDao()
    @Provides fun provideWorkoutSessionDao(db: AppDatabase): WorkoutSessionDao = db.workoutSessionDao()
    @Provides fun provideNutritionDao(db: AppDatabase): NutritionDao = db.nutritionDao()
}
