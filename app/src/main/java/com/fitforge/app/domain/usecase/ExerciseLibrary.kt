package com.fitforge.app.domain.usecase

import com.fitforge.app.domain.model.*

object ExerciseLibrary {

    val all: List<Exercise> = listOf(
        // CHEST
        Exercise("bench_press", "Barbell Bench Press", ExerciseCategory.CHEST,
            listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
            Equipment.BARBELL, Difficulty.INTERMEDIATE,
            instructions = "Lie flat, grip barbell shoulder-width, lower to chest, press up."),
        Exercise("pushup", "Push-up", ExerciseCategory.CHEST,
            listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.CORE),
            Equipment.NONE, Difficulty.BEGINNER,
            instructions = "Plank position, lower chest to floor, push back up."),
        Exercise("incline_db_press", "Incline Dumbbell Press", ExerciseCategory.CHEST,
            listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS), Equipment.DUMBBELLS, Difficulty.INTERMEDIATE),
        Exercise("cable_flye", "Cable Chest Fly", ExerciseCategory.CHEST,
            listOf(MuscleGroup.CHEST), Equipment.CABLE, Difficulty.INTERMEDIATE),

        // BACK
        Exercise("pullup", "Pull-up", ExerciseCategory.BACK,
            listOf(MuscleGroup.LATS, MuscleGroup.BICEPS, MuscleGroup.UPPER_BACK),
            Equipment.PULL_UP_BAR, Difficulty.INTERMEDIATE,
            instructions = "Hang from bar, pull chin above bar, lower with control."),
        Exercise("bent_row", "Bent-over Barbell Row", ExerciseCategory.BACK,
            listOf(MuscleGroup.UPPER_BACK, MuscleGroup.LATS, MuscleGroup.BICEPS),
            Equipment.BARBELL, Difficulty.INTERMEDIATE),
        Exercise("lat_pulldown", "Lat Pulldown", ExerciseCategory.BACK,
            listOf(MuscleGroup.LATS, MuscleGroup.BICEPS), Equipment.CABLE, Difficulty.BEGINNER),
        Exercise("deadlift", "Deadlift", ExerciseCategory.BACK,
            listOf(MuscleGroup.LOWER_BACK, MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.TRAPS),
            Equipment.BARBELL, Difficulty.ADVANCED,
            instructions = "Hip-hinge, neutral spine, drive through heels to lock out."),

        // LEGS
        Exercise("squat", "Barbell Back Squat", ExerciseCategory.LEGS,
            listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            Equipment.BARBELL, Difficulty.INTERMEDIATE,
            instructions = "Bar on traps, feet shoulder-width, squat to parallel, drive up."),
        Exercise("goblet_squat", "Goblet Squat", ExerciseCategory.LEGS,
            listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES), Equipment.DUMBBELLS, Difficulty.BEGINNER),
        Exercise("lunges", "Dumbbell Lunges", ExerciseCategory.LEGS,
            listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            Equipment.DUMBBELLS, Difficulty.BEGINNER),
        Exercise("rdl", "Romanian Deadlift", ExerciseCategory.LEGS,
            listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK),
            Equipment.BARBELL, Difficulty.INTERMEDIATE),
        Exercise("leg_press", "Leg Press", ExerciseCategory.LEGS,
            listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES), Equipment.MACHINE, Difficulty.BEGINNER),

        // SHOULDERS
        Exercise("ohp", "Overhead Press", ExerciseCategory.SHOULDERS,
            listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS, MuscleGroup.TRAPS),
            Equipment.BARBELL, Difficulty.INTERMEDIATE),
        Exercise("lateral_raise", "Lateral Raise", ExerciseCategory.SHOULDERS,
            listOf(MuscleGroup.SHOULDERS), Equipment.DUMBBELLS, Difficulty.BEGINNER),
        Exercise("face_pull", "Face Pull", ExerciseCategory.SHOULDERS,
            listOf(MuscleGroup.TRAPS, MuscleGroup.SHOULDERS), Equipment.CABLE, Difficulty.BEGINNER),

        // ARMS
        Exercise("bicep_curl", "Dumbbell Bicep Curl", ExerciseCategory.ARMS,
            listOf(MuscleGroup.BICEPS), Equipment.DUMBBELLS, Difficulty.BEGINNER),
        Exercise("tricep_dip", "Tricep Dip", ExerciseCategory.ARMS,
            listOf(MuscleGroup.TRICEPS, MuscleGroup.CHEST), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("hammer_curl", "Hammer Curl", ExerciseCategory.ARMS,
            listOf(MuscleGroup.BICEPS, MuscleGroup.FOREARMS), Equipment.DUMBBELLS, Difficulty.BEGINNER),
        Exercise("skull_crusher", "Skull Crusher", ExerciseCategory.ARMS,
            listOf(MuscleGroup.TRICEPS), Equipment.BARBELL, Difficulty.INTERMEDIATE),

        // CORE
        Exercise("plank", "Plank", ExerciseCategory.CORE,
            listOf(MuscleGroup.CORE), Equipment.NONE, Difficulty.BEGINNER,
            instructions = "Forearms on floor, body in straight line, hold."),
        Exercise("crunch", "Crunch", ExerciseCategory.CORE,
            listOf(MuscleGroup.CORE), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("russian_twist", "Russian Twist", ExerciseCategory.CORE,
            listOf(MuscleGroup.CORE), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("dead_bug", "Dead Bug", ExerciseCategory.CORE,
            listOf(MuscleGroup.CORE, MuscleGroup.HIP_FLEXORS), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("hanging_knee_raise", "Hanging Knee Raise", ExerciseCategory.CORE,
            listOf(MuscleGroup.CORE, MuscleGroup.HIP_FLEXORS), Equipment.PULL_UP_BAR, Difficulty.INTERMEDIATE),

        // CARDIO
        Exercise("treadmill_run", "Treadmill Run", ExerciseCategory.CARDIO,
            listOf(), Equipment.CARDIO_MACHINE, Difficulty.BEGINNER, isCardio = true),
        Exercise("bike", "Stationary Bike", ExerciseCategory.CARDIO,
            listOf(), Equipment.CARDIO_MACHINE, Difficulty.BEGINNER, isCardio = true),
        Exercise("jump_rope", "Jump Rope", ExerciseCategory.CARDIO,
            listOf(), Equipment.NONE, Difficulty.BEGINNER, isCardio = true),
        Exercise("burpee", "Burpee", ExerciseCategory.FULL_BODY,
            listOf(MuscleGroup.CORE, MuscleGroup.CHEST, MuscleGroup.QUADS),
            Equipment.NONE, Difficulty.INTERMEDIATE, isCardio = true),
        Exercise("mountain_climber", "Mountain Climber", ExerciseCategory.FULL_BODY,
            listOf(MuscleGroup.CORE, MuscleGroup.QUADS), Equipment.NONE, Difficulty.BEGINNER, isCardio = true),

        // FLEXIBILITY
        Exercise("hip_flexor_stretch", "Hip Flexor Stretch", ExerciseCategory.FLEXIBILITY,
            listOf(MuscleGroup.HIP_FLEXORS), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("pigeon_pose", "Pigeon Pose", ExerciseCategory.FLEXIBILITY,
            listOf(MuscleGroup.GLUTES, MuscleGroup.HIP_FLEXORS), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("cat_cow", "Cat-Cow Stretch", ExerciseCategory.FLEXIBILITY,
            listOf(MuscleGroup.LOWER_BACK, MuscleGroup.CORE), Equipment.NONE, Difficulty.BEGINNER),
        Exercise("world_greatest_stretch", "World's Greatest Stretch", ExerciseCategory.FLEXIBILITY,
            listOf(MuscleGroup.HIP_FLEXORS, MuscleGroup.CORE, MuscleGroup.SHOULDERS),
            Equipment.NONE, Difficulty.BEGINNER)
    )

    fun byCategory(category: ExerciseCategory) = all.filter { it.category == category }
    fun byDifficulty(difficulty: Difficulty) = all.filter { it.difficulty == difficulty }
    fun cardio() = all.filter { it.isCardio }
    fun noEquipment() = all.filter { it.equipment == Equipment.NONE }
}
