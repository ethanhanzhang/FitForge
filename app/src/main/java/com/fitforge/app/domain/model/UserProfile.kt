package com.fitforge.app.domain.model

data class UserProfile(
    val id: Int = 1,
    val name: String = "",
    val age: Int = 0,
    val sex: Sex = Sex.UNSPECIFIED,
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val primaryGoal: TrainingGoal = TrainingGoal.GENERAL_FITNESS,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER
) {
    val bmi: Float get() = if (heightCm > 0) weightKg / ((heightCm / 100f) * (heightCm / 100f)) else 0f

    val bmr: Float get() = when (sex) {
        Sex.MALE -> 88.362f + (13.397f * weightKg) + (4.799f * heightCm) - (5.677f * age)
        Sex.FEMALE -> 447.593f + (9.247f * weightKg) + (3.098f * heightCm) - (4.330f * age)
        Sex.UNSPECIFIED -> 500f + (11f * weightKg) + (4f * heightCm) - (5f * age)
    }

    val tdee: Float get() = bmr * activityLevel.multiplier
}

enum class Sex { MALE, FEMALE, UNSPECIFIED }

enum class FitnessLevel(val label: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced")
}

enum class ActivityLevel(val label: String, val multiplier: Float) {
    SEDENTARY("Sedentary", 1.2f),
    LIGHT("Lightly Active", 1.375f),
    MODERATE("Moderately Active", 1.55f),
    VERY_ACTIVE("Very Active", 1.725f),
    EXTRA_ACTIVE("Extra Active", 1.9f)
}
