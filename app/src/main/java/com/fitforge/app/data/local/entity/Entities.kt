package com.fitforge.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fitforge.app.domain.model.*

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val sex: String,
    val heightCm: Float,
    val weightKg: Float,
    val primaryGoal: String,
    val activityLevel: String,
    val fitnessLevel: String
) {
    fun toDomain() = UserProfile(
        id = id,
        name = name,
        age = age,
        sex = Sex.valueOf(sex),
        heightCm = heightCm,
        weightKg = weightKg,
        primaryGoal = TrainingGoal.valueOf(primaryGoal),
        activityLevel = ActivityLevel.valueOf(activityLevel),
        fitnessLevel = FitnessLevel.valueOf(fitnessLevel)
    )
}

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    name = name,
    age = age,
    sex = sex.name,
    heightCm = heightCm,
    weightKg = weightKg,
    primaryGoal = primaryGoal.name,
    activityLevel = activityLevel.name,
    fitnessLevel = fitnessLevel.name
)

@Entity(tableName = "sleep_log")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,          // ISO-8601 LocalDate string
    val durationHours: Float,
    val quality: Int,
    val bedTimeHour: Int,
    val wakeTimeHour: Int,
    val notes: String
) {
    fun toDomain() = SleepLog(
        id = id,
        date = java.time.LocalDate.parse(date),
        durationHours = durationHours,
        quality = quality,
        bedTimeHour = bedTimeHour,
        wakeTimeHour = wakeTimeHour,
        notes = notes
    )
}

fun SleepLog.toEntity() = SleepLogEntity(
    id = id,
    date = date.toString(),
    durationHours = durationHours,
    quality = quality,
    bedTimeHour = bedTimeHour,
    wakeTimeHour = wakeTimeHour,
    notes = notes
)

@Entity(tableName = "daily_checkin")
data class DailyCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mentalState: Int,
    val physicalState: Int,
    val energyLevel: Int,
    val stressLevel: Int,
    val notes: String
) {
    fun toDomain() = DailyCheckIn(
        id = id,
        date = java.time.LocalDate.parse(date),
        mentalState = mentalState,
        physicalState = physicalState,
        energyLevel = energyLevel,
        stressLevel = stressLevel,
        notes = notes
    )
}

fun DailyCheckIn.toEntity() = DailyCheckInEntity(
    id = id,
    date = date.toString(),
    mentalState = mentalState,
    physicalState = physicalState,
    energyLevel = energyLevel,
    stressLevel = stressLevel,
    notes = notes
)

@Entity(tableName = "workout_session")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val planId: String,
    val dayLabel: String,
    val workoutType: String,
    val durationMinutes: Int,
    val notes: String,
    val completed: Boolean
)

@Entity(tableName = "nutrition_log")
data class NutritionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val mealName: String,
    val calories: Int,
    val proteinG: Float,
    val carbsG: Float,
    val fatG: Float,
    val notes: String
)
