package com.fitforge.app.domain.model

data class Exercise(
    val id: String,
    val name: String,
    val category: ExerciseCategory,
    val muscleGroups: List<MuscleGroup>,
    val equipment: Equipment,
    val difficulty: Difficulty,
    val isCardio: Boolean = false,
    val instructions: String = ""
)

enum class ExerciseCategory(val label: String) {
    CHEST("Chest"),
    BACK("Back"),
    LEGS("Legs"),
    SHOULDERS("Shoulders"),
    ARMS("Arms"),
    CORE("Core"),
    CARDIO("Cardio"),
    FULL_BODY("Full Body"),
    FLEXIBILITY("Flexibility")
}

enum class MuscleGroup {
    CHEST, UPPER_BACK, LOWER_BACK, QUADS, HAMSTRINGS, GLUTES, CALVES,
    SHOULDERS, BICEPS, TRICEPS, FOREARMS, CORE, LATS, TRAPS, HIP_FLEXORS
}

enum class Equipment(val label: String) {
    NONE("No Equipment"),
    DUMBBELLS("Dumbbells"),
    BARBELL("Barbell"),
    CABLE("Cable Machine"),
    MACHINE("Machine"),
    PULL_UP_BAR("Pull-up Bar"),
    RESISTANCE_BAND("Resistance Band"),
    CARDIO_MACHINE("Cardio Machine")
}

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

data class ExerciseSet(
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val weightKg: Float? = null,
    val restSeconds: Int = 60,
    val completed: Boolean = false
)
