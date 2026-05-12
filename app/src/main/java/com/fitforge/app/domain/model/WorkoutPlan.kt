package com.fitforge.app.domain.model

data class WorkoutPlan(
    val id: String,
    val name: String,
    val goal: TrainingGoal,
    val durationWeeks: Int,
    val daysPerWeek: Int,
    val description: String,
    val weeks: List<TrainingWeek>
)

data class TrainingWeek(
    val weekNumber: Int,
    val focus: String,
    val days: List<TrainingDay>
)

data class TrainingDay(
    val dayOfWeek: Int,        // 1 = Monday
    val label: String,
    val workoutType: WorkoutType,
    val exercises: List<PlannedExercise>
)

data class PlannedExercise(
    val exercise: Exercise,
    val sets: Int,
    val reps: IntRange? = null,
    val durationSeconds: Int? = null,
    val restSeconds: Int = 60,
    val notes: String = ""
)

enum class WorkoutType(val label: String) {
    STRENGTH("Strength"),
    CARDIO("Cardio"),
    HIIT("HIIT"),
    FLEXIBILITY("Flexibility"),
    ACTIVE_RECOVERY("Active Recovery"),
    REST("Rest Day"),
    FULL_BODY("Full Body")
}
