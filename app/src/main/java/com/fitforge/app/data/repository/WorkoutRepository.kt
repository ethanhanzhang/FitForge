package com.fitforge.app.data.repository

import com.fitforge.app.data.local.dao.WorkoutSessionDao
import com.fitforge.app.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(private val dao: WorkoutSessionDao) {

    fun getRecentSessions(): Flow<List<WorkoutSessionEntity>> = dao.getRecent()

    suspend fun logSession(
        planId: String,
        dayLabel: String,
        workoutType: String,
        durationMinutes: Int,
        notes: String = "",
        completed: Boolean = true
    ): Long = dao.insert(
        WorkoutSessionEntity(
            date = LocalDate.now().toString(),
            planId = planId,
            dayLabel = dayLabel,
            workoutType = workoutType,
            durationMinutes = durationMinutes,
            notes = notes,
            completed = completed
        )
    )

    suspend fun completedWorkoutCount(): Int = dao.completedCount()
}
