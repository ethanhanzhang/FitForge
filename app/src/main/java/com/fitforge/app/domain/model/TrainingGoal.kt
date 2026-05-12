package com.fitforge.app.domain.model

enum class TrainingGoal(
    val label: String,
    val description: String,
    val icon: String
) {
    WEIGHT_LOSS(
        "Weight Loss",
        "Burn fat through cardio and caloric deficit",
        "🔥"
    ),
    MUSCLE_GAIN(
        "Muscle Gain",
        "Build strength and size with progressive overload",
        "💪"
    ),
    ENDURANCE(
        "Endurance",
        "Improve cardiovascular fitness and stamina",
        "🏃"
    ),
    FLEXIBILITY(
        "Flexibility & Mobility",
        "Increase range of motion and reduce injury risk",
        "🧘"
    ),
    GENERAL_FITNESS(
        "General Fitness",
        "Balanced program for overall health and wellbeing",
        "⚡"
    ),
    ATHLETIC_PERFORMANCE(
        "Athletic Performance",
        "Sport-specific training to maximize performance",
        "🏆"
    )
}
