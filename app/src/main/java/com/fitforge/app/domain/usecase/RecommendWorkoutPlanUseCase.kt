package com.fitforge.app.domain.usecase

import com.fitforge.app.domain.model.*
import javax.inject.Inject

class RecommendWorkoutPlanUseCase @Inject constructor() {

    fun execute(profile: UserProfile): WorkoutPlan {
        val exercises = ExerciseLibrary
        val level = profile.fitnessLevel

        return when (profile.primaryGoal) {
            TrainingGoal.MUSCLE_GAIN -> buildMusclePlan(exercises, level)
            TrainingGoal.WEIGHT_LOSS -> buildWeightLossPlan(exercises, level)
            TrainingGoal.ENDURANCE -> buildEndurancePlan(exercises, level)
            TrainingGoal.FLEXIBILITY -> buildFlexibilityPlan(exercises, level)
            TrainingGoal.ATHLETIC_PERFORMANCE -> buildAthleticPlan(exercises, level)
            TrainingGoal.GENERAL_FITNESS -> buildGeneralPlan(exercises, level)
        }
    }

    // Adjust today's recommended intensity based on sleep and check-in data
    fun adjustIntensity(
        plan: WorkoutPlan,
        sleep: SleepLog?,
        checkIn: DailyCheckIn?
    ): IntensityAdjustment {
        val sleepModifier = sleep?.recoveryImpact?.intensityModifier ?: 1.0f
        val readiness = checkIn?.readinessScore ?: 3

        val checkInModifier = when (readiness) {
            1 -> 0.0f   // rest day
            2 -> 0.5f
            3 -> 0.85f
            4 -> 1.0f
            5 -> 1.1f   // slight volume bump on peak days
            else -> 1.0f
        }

        val combinedModifier = (sleepModifier + checkInModifier) / 2f

        return IntensityAdjustment(
            modifier = combinedModifier,
            recommendation = when {
                combinedModifier < 0.3f -> "Rest today — your body needs recovery."
                combinedModifier < 0.6f -> "Light activity only — a walk or gentle stretching."
                combinedModifier < 0.85f -> "Moderate session — reduce sets by 20%."
                combinedModifier <= 1.0f -> "Train as planned."
                else -> "You're peaking — consider an extra set or slight weight increase."
            }
        )
    }

    // ─── Plan Builders ────────────────────────────────────────────────────────

    private fun buildMusclePlan(ex: ExerciseLibrary, level: FitnessLevel): WorkoutPlan {
        val sets = if (level == FitnessLevel.BEGINNER) 3 else 4
        val rest = if (level == FitnessLevel.ADVANCED) 90 else 120

        return WorkoutPlan(
            id = "muscle_gain",
            name = "Hypertrophy Program",
            goal = TrainingGoal.MUSCLE_GAIN,
            durationWeeks = 8,
            daysPerWeek = 4,
            description = "4-day upper/lower split focused on progressive overload to maximize muscle growth.",
            weeks = (1..8).map { week ->
                TrainingWeek(
                    weekNumber = week,
                    focus = if (week <= 4) "Foundation (Week $week)" else "Overload (Week $week)",
                    days = listOf(
                        TrainingDay(1, "Upper A — Push", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "bench_press" }, sets, 6..10, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "ohp" }, sets, 8..12, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "incline_db_press" }, sets, 10..15, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "lateral_raise" }, 3, 12..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "skull_crusher" }, 3, 10..12, restSeconds = 60)
                        )),
                        TrainingDay(2, "Lower A — Squat", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "squat" }, sets, 5..8, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "rdl" }, sets, 8..12, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "lunges" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "plank" }, 3, durationSeconds = 45, restSeconds = 60)
                        )),
                        TrainingDay(3, "Rest Day", WorkoutType.REST, emptyList()),
                        TrainingDay(4, "Upper B — Pull", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "pullup" }, sets, 5..10, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "bent_row" }, sets, 6..10, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "lat_pulldown" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "bicep_curl" }, 3, 10..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "face_pull" }, 3, 15..20, restSeconds = 60)
                        )),
                        TrainingDay(5, "Lower B — Hinge", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "deadlift" }, sets, 4..6, restSeconds = rest + 30),
                            PlannedExercise(ex.all.first { it.id == "leg_press" }, sets, 10..15, restSeconds = rest),
                            PlannedExercise(ex.all.first { it.id == "goblet_squat" }, 3, 12..15, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "hanging_knee_raise" }, 3, 10..15, restSeconds = 60)
                        )),
                        TrainingDay(6, "Active Recovery", WorkoutType.ACTIVE_RECOVERY, listOf(
                            PlannedExercise(ex.all.first { it.id == "cat_cow" }, 2, durationSeconds = 60, restSeconds = 30),
                            PlannedExercise(ex.all.first { it.id == "hip_flexor_stretch" }, 2, durationSeconds = 60, restSeconds = 30),
                            PlannedExercise(ex.all.first { it.id == "pigeon_pose" }, 2, durationSeconds = 60, restSeconds = 30)
                        )),
                        TrainingDay(7, "Rest Day", WorkoutType.REST, emptyList())
                    )
                )
            }
        )
    }

    private fun buildWeightLossPlan(ex: ExerciseLibrary, level: FitnessLevel): WorkoutPlan {
        return WorkoutPlan(
            id = "weight_loss",
            name = "Fat Loss Program",
            goal = TrainingGoal.WEIGHT_LOSS,
            durationWeeks = 8,
            daysPerWeek = 5,
            description = "5-day program combining HIIT, metabolic conditioning, and moderate strength to maximize calorie burn.",
            weeks = (1..8).map { week ->
                TrainingWeek(
                    weekNumber = week,
                    focus = if (week <= 2) "Foundation" else if (week <= 5) "Intensification" else "Peak",
                    days = listOf(
                        TrainingDay(1, "Full Body Strength", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "goblet_squat" }, 4, 12..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "pushup" }, 4, 12..20, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "bent_row" }, 4, 12..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "lunges" }, 3, 12..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "plank" }, 3, durationSeconds = 45, restSeconds = 45)
                        )),
                        TrainingDay(2, "HIIT Cardio", WorkoutType.HIIT, listOf(
                            PlannedExercise(ex.all.first { it.id == "jump_rope" }, 5, durationSeconds = 40, restSeconds = 20),
                            PlannedExercise(ex.all.first { it.id == "burpee" }, 5, 10..15, restSeconds = 20),
                            PlannedExercise(ex.all.first { it.id == "mountain_climber" }, 5, durationSeconds = 40, restSeconds = 20),
                            PlannedExercise(ex.all.first { it.id == "goblet_squat" }, 5, 15..20, restSeconds = 20)
                        )),
                        TrainingDay(3, "Steady-State Cardio", WorkoutType.CARDIO, listOf(
                            PlannedExercise(ex.all.first { it.id == "treadmill_run" }, 1, durationSeconds = 2400, restSeconds = 0,
                                notes = "Zone 2: 60–70% max heart rate")
                        )),
                        TrainingDay(4, "Upper Body + Core", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "lat_pulldown" }, 3, 12..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "incline_db_press" }, 3, 12..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "lateral_raise" }, 3, 15..20, restSeconds = 45),
                            PlannedExercise(ex.all.first { it.id == "russian_twist" }, 3, 20..30, restSeconds = 45),
                            PlannedExercise(ex.all.first { it.id == "dead_bug" }, 3, 10..12, restSeconds = 45)
                        )),
                        TrainingDay(5, "Lower Body Metabolic", WorkoutType.HIIT, listOf(
                            PlannedExercise(ex.all.first { it.id == "squat" }, 4, 15..20, restSeconds = 45),
                            PlannedExercise(ex.all.first { it.id == "rdl" }, 4, 12..15, restSeconds = 45),
                            PlannedExercise(ex.all.first { it.id == "lunges" }, 4, 12..15, restSeconds = 45),
                            PlannedExercise(ex.all.first { it.id == "mountain_climber" }, 4, durationSeconds = 30, restSeconds = 30)
                        )),
                        TrainingDay(6, "Active Recovery / Walk", WorkoutType.ACTIVE_RECOVERY, listOf(
                            PlannedExercise(ex.all.first { it.id == "cat_cow" }, 2, durationSeconds = 60, restSeconds = 30),
                            PlannedExercise(ex.all.first { it.id == "pigeon_pose" }, 2, durationSeconds = 60, restSeconds = 30)
                        )),
                        TrainingDay(7, "Rest Day", WorkoutType.REST, emptyList())
                    )
                )
            }
        )
    }

    private fun buildEndurancePlan(ex: ExerciseLibrary, level: FitnessLevel): WorkoutPlan {
        return WorkoutPlan(
            id = "endurance",
            name = "Endurance Builder",
            goal = TrainingGoal.ENDURANCE,
            durationWeeks = 8,
            daysPerWeek = 5,
            description = "Zone 2 cardio foundation with tempo runs and supplemental strength to build aerobic capacity.",
            weeks = (1..8).map { week ->
                TrainingWeek(
                    weekNumber = week,
                    focus = "Week $week — ${if (week <= 3) "Base Building" else if (week <= 6) "Build" else "Peak"}",
                    days = listOf(
                        TrainingDay(1, "Easy Run / Zone 2", WorkoutType.CARDIO, listOf(
                            PlannedExercise(ex.all.first { it.id == "treadmill_run" }, 1,
                                durationSeconds = (20 + week * 3) * 60, restSeconds = 0,
                                notes = "Zone 2: conversational pace")
                        )),
                        TrainingDay(2, "Strength + Core", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "squat" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "deadlift" }, 3, 8..10, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "plank" }, 3, durationSeconds = 60, restSeconds = 45)
                        )),
                        TrainingDay(3, "Tempo / Threshold", WorkoutType.CARDIO, listOf(
                            PlannedExercise(ex.all.first { it.id == "treadmill_run" }, 1,
                                durationSeconds = 30 * 60, restSeconds = 0,
                                notes = "Warm up 10 min, 20 min at lactate threshold, cool down 10 min")
                        )),
                        TrainingDay(4, "Cross-Training", WorkoutType.CARDIO, listOf(
                            PlannedExercise(ex.all.first { it.id == "bike" }, 1, durationSeconds = 45 * 60, restSeconds = 0)
                        )),
                        TrainingDay(5, "Long Slow Distance", WorkoutType.CARDIO, listOf(
                            PlannedExercise(ex.all.first { it.id == "treadmill_run" }, 1,
                                durationSeconds = (35 + week * 5) * 60, restSeconds = 0,
                                notes = "Easy pace — focus on time on feet")
                        )),
                        TrainingDay(6, "Mobility + Stretch", WorkoutType.FLEXIBILITY, listOf(
                            PlannedExercise(ex.all.first { it.id == "world_greatest_stretch" }, 3, reps = 6..8, restSeconds = 30),
                            PlannedExercise(ex.all.first { it.id == "hip_flexor_stretch" }, 2, durationSeconds = 90, restSeconds = 30)
                        )),
                        TrainingDay(7, "Rest Day", WorkoutType.REST, emptyList())
                    )
                )
            }
        )
    }

    private fun buildFlexibilityPlan(ex: ExerciseLibrary, level: FitnessLevel): WorkoutPlan {
        return WorkoutPlan(
            id = "flexibility",
            name = "Mobility & Flexibility",
            goal = TrainingGoal.FLEXIBILITY,
            durationWeeks = 6,
            daysPerWeek = 6,
            description = "Daily mobility work with progressive stretching to improve range of motion and reduce injury risk.",
            weeks = (1..6).map { week ->
                TrainingWeek(
                    weekNumber = week,
                    focus = "Week $week",
                    days = (1..7).map { day ->
                        if (day == 7)
                            TrainingDay(day, "Rest Day", WorkoutType.REST, emptyList())
                        else
                            TrainingDay(day, "Daily Mobility", WorkoutType.FLEXIBILITY, listOf(
                                PlannedExercise(ex.all.first { it.id == "cat_cow" }, 2, durationSeconds = 60, restSeconds = 15),
                                PlannedExercise(ex.all.first { it.id == "world_greatest_stretch" }, 3, reps = 6..10, restSeconds = 20),
                                PlannedExercise(ex.all.first { it.id == "pigeon_pose" }, 2, durationSeconds = 90, restSeconds = 20),
                                PlannedExercise(ex.all.first { it.id == "hip_flexor_stretch" }, 2, durationSeconds = 60, restSeconds = 20)
                            ))
                    }
                )
            }
        )
    }

    private fun buildAthleticPlan(ex: ExerciseLibrary, level: FitnessLevel): WorkoutPlan {
        return WorkoutPlan(
            id = "athletic",
            name = "Athletic Performance",
            goal = TrainingGoal.ATHLETIC_PERFORMANCE,
            durationWeeks = 8,
            daysPerWeek = 5,
            description = "Power, speed, and strength program for sport-specific athletic development.",
            weeks = (1..8).map { week ->
                TrainingWeek(
                    weekNumber = week,
                    focus = if (week <= 3) "Power Foundation" else if (week <= 6) "Speed-Strength" else "Peaking",
                    days = listOf(
                        TrainingDay(1, "Power Lower", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "squat" }, 5, 3..5, restSeconds = 180),
                            PlannedExercise(ex.all.first { it.id == "deadlift" }, 4, 3..5, restSeconds = 180),
                            PlannedExercise(ex.all.first { it.id == "lunges" }, 3, 8..10, restSeconds = 90)
                        )),
                        TrainingDay(2, "Power Upper", WorkoutType.STRENGTH, listOf(
                            PlannedExercise(ex.all.first { it.id == "bench_press" }, 5, 3..5, restSeconds = 180),
                            PlannedExercise(ex.all.first { it.id == "bent_row" }, 5, 3..5, restSeconds = 180),
                            PlannedExercise(ex.all.first { it.id == "ohp" }, 4, 5..8, restSeconds = 120)
                        )),
                        TrainingDay(3, "Speed + Conditioning", WorkoutType.HIIT, listOf(
                            PlannedExercise(ex.all.first { it.id == "burpee" }, 6, 8..10, restSeconds = 30),
                            PlannedExercise(ex.all.first { it.id == "mountain_climber" }, 6, durationSeconds = 30, restSeconds = 20),
                            PlannedExercise(ex.all.first { it.id == "jump_rope" }, 6, durationSeconds = 45, restSeconds = 15)
                        )),
                        TrainingDay(4, "Active Recovery", WorkoutType.ACTIVE_RECOVERY, listOf(
                            PlannedExercise(ex.all.first { it.id == "cat_cow" }, 2, durationSeconds = 60, restSeconds = 20),
                            PlannedExercise(ex.all.first { it.id == "world_greatest_stretch" }, 3, reps = 8..10, restSeconds = 20)
                        )),
                        TrainingDay(5, "Full Body Power", WorkoutType.FULL_BODY, listOf(
                            PlannedExercise(ex.all.first { it.id == "deadlift" }, 4, 4..6, restSeconds = 180),
                            PlannedExercise(ex.all.first { it.id == "bench_press" }, 4, 4..6, restSeconds = 180),
                            PlannedExercise(ex.all.first { it.id == "pullup" }, 4, 6..8, restSeconds = 120)
                        )),
                        TrainingDay(6, "Rest", WorkoutType.REST, emptyList()),
                        TrainingDay(7, "Rest", WorkoutType.REST, emptyList())
                    )
                )
            }
        )
    }

    private fun buildGeneralPlan(ex: ExerciseLibrary, level: FitnessLevel): WorkoutPlan {
        return WorkoutPlan(
            id = "general",
            name = "General Fitness",
            goal = TrainingGoal.GENERAL_FITNESS,
            durationWeeks = 8,
            daysPerWeek = 3,
            description = "Balanced 3-day full-body program for overall health, accessible for all fitness levels.",
            weeks = (1..8).map { week ->
                TrainingWeek(
                    weekNumber = week,
                    focus = "Week $week",
                    days = listOf(
                        TrainingDay(1, "Full Body A", WorkoutType.FULL_BODY, listOf(
                            PlannedExercise(ex.all.first { it.id == "goblet_squat" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "pushup" }, 3, 8..15, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "lat_pulldown" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "plank" }, 3, durationSeconds = 30, restSeconds = 60)
                        )),
                        TrainingDay(3, "Full Body B", WorkoutType.FULL_BODY, listOf(
                            PlannedExercise(ex.all.first { it.id == "deadlift" }, 3, 8..10, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "incline_db_press" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "bent_row" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "crunch" }, 3, 15..20, restSeconds = 60)
                        )),
                        TrainingDay(5, "Full Body C + Cardio", WorkoutType.FULL_BODY, listOf(
                            PlannedExercise(ex.all.first { it.id == "lunges" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "ohp" }, 3, 10..12, restSeconds = 90),
                            PlannedExercise(ex.all.first { it.id == "bicep_curl" }, 3, 10..15, restSeconds = 60),
                            PlannedExercise(ex.all.first { it.id == "treadmill_run" }, 1, durationSeconds = 20 * 60, restSeconds = 0)
                        ))
                    )
                )
            }
        )
    }
}

data class IntensityAdjustment(
    val modifier: Float,
    val recommendation: String
)
