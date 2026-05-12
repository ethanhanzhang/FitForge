package com.fitforge.app.domain.model

import java.time.LocalDate

data class SleepLog(
    val id: Long = 0,
    val date: LocalDate = LocalDate.now(),
    val durationHours: Float = 0f,
    val quality: Int = 3,   // 1–5
    val bedTimeHour: Int = 22,
    val wakeTimeHour: Int = 6,
    val notes: String = ""
) {
    val isAdequate: Boolean get() = durationHours >= 7f
    val recoveryImpact: RecoveryImpact get() = when {
        durationHours >= 8f && quality >= 4 -> RecoveryImpact.OPTIMAL
        durationHours >= 7f && quality >= 3 -> RecoveryImpact.GOOD
        durationHours >= 6f -> RecoveryImpact.MODERATE
        else -> RecoveryImpact.POOR
    }
}

enum class RecoveryImpact(val label: String, val intensityModifier: Float) {
    OPTIMAL("Optimal Recovery", 1.0f),
    GOOD("Good Recovery", 0.9f),
    MODERATE("Moderate Recovery", 0.75f),
    POOR("Poor Recovery — reduce intensity", 0.5f)
}
