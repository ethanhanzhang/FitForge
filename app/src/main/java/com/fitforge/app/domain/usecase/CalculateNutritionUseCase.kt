package com.fitforge.app.domain.usecase

import com.fitforge.app.domain.model.TrainingGoal
import com.fitforge.app.domain.model.UserProfile
import javax.inject.Inject

class CalculateNutritionUseCase @Inject constructor() {

    fun execute(profile: UserProfile): NutritionTargets {
        val tdee = profile.tdee

        val calorieTarget = when (profile.primaryGoal) {
            TrainingGoal.WEIGHT_LOSS -> (tdee * 0.80f).toInt()        // 20% deficit
            TrainingGoal.MUSCLE_GAIN -> (tdee * 1.12f).toInt()        // 12% surplus
            TrainingGoal.ENDURANCE -> (tdee * 1.05f).toInt()          // slight surplus for fueling
            else -> tdee.toInt()                                        // maintenance
        }

        // Protein: higher for muscle gain, moderate otherwise
        val proteinG = when (profile.primaryGoal) {
            TrainingGoal.MUSCLE_GAIN -> (profile.weightKg * 2.2f).toInt()
            TrainingGoal.WEIGHT_LOSS -> (profile.weightKg * 2.0f).toInt()
            TrainingGoal.ENDURANCE -> (profile.weightKg * 1.6f).toInt()
            else -> (profile.weightKg * 1.8f).toInt()
        }

        val fatG = ((calorieTarget * 0.25f) / 9f).toInt()
        val carbCalories = calorieTarget - (proteinG * 4) - (fatG * 9)
        val carbG = (carbCalories / 4f).toInt().coerceAtLeast(0)

        return NutritionTargets(
            calories = calorieTarget,
            proteinG = proteinG,
            carbsG = carbG,
            fatG = fatG,
            waterMl = (profile.weightKg * 35).toInt()
        )
    }
}

data class NutritionTargets(
    val calories: Int,
    val proteinG: Int,
    val carbsG: Int,
    val fatG: Int,
    val waterMl: Int
) {
    val proteinCalories get() = proteinG * 4
    val carbCalories get() = carbsG * 4
    val fatCalories get() = fatG * 9
}
