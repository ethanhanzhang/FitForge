package com.fitforge.app.domain.model

import java.time.LocalDate

data class DailyCheckIn(
    val id: Long = 0,
    val date: LocalDate = LocalDate.now(),
    val mentalState: Int = 3,      // 1–5: 1 = very poor, 5 = excellent
    val physicalState: Int = 3,    // 1–5: 1 = very sore/tired, 5 = feeling great
    val energyLevel: Int = 3,      // 1–5
    val stressLevel: Int = 3,      // 1–5 (5 = very stressed)
    val notes: String = ""
) {
    val readinessScore: Int get() {
        val stress = 6 - stressLevel  // invert stress
        return ((mentalState + physicalState + energyLevel + stress) / 4.0).toInt()
    }

    val readinessLabel: String get() = when (readinessScore) {
        1 -> "Rest Day Recommended"
        2 -> "Light Activity Only"
        3 -> "Moderate Training"
        4 -> "Train Hard"
        5 -> "Peak Performance"
        else -> "Train Moderate"
    }
}
