package com.fitforge.app.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    planId: String,
    onBack: () -> Unit,
    vm: WorkoutViewModel = hiltViewModel()
) {
    val plan by vm.plan.collectAsState()
    val logSuccess by vm.logSuccess.collectAsState()

    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var showLogDialog by remember { mutableStateOf(false) }

    LaunchedEffect(logSuccess) {
        if (logSuccess) {
            showLogDialog = false
            vm.resetLogSuccess()
        }
    }

    if (plan == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val p = plan!!
    val currentWeek = p.weeks.firstOrNull() ?: return
    val workoutDays = currentWeek.days.filter { it.workoutType != WorkoutType.REST }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(p.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showLogDialog = true }) { Text("Log Session") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(Modifier.height(4.dp))
                Text("Week 1 — ${currentWeek.focus}", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                // Day selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    workoutDays.forEachIndexed { idx, day ->
                        FilterChip(
                            selected = selectedDayIndex == idx,
                            onClick = { selectedDayIndex = idx },
                            label = { Text("D${day.dayOfWeek}") }
                        )
                    }
                }
            }

            if (workoutDays.isNotEmpty()) {
                val day = workoutDays[selectedDayIndex]
                item {
                    Text(day.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(day.workoutType.label, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(4.dp))
                }
                itemsIndexed(day.exercises) { index, planned ->
                    ExerciseCard(index = index + 1, planned = planned)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showLogDialog && plan != null) {
        val day = plan!!.weeks.firstOrNull()?.days?.filter { it.workoutType != WorkoutType.REST }
            ?.getOrNull(selectedDayIndex)
        if (day != null) {
            LogWorkoutDialog(
                day = day,
                onDismiss = { showLogDialog = false },
                onConfirm = { minutes, notes -> vm.logWorkout(day, minutes, notes) }
            )
        }
    }
}

@Composable
private fun ExerciseCard(index: Int, planned: PlannedExercise) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primary) {
                Text("$index", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(planned.exercise.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(planned.exercise.category.label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                val detail = buildString {
                    append("${planned.sets} sets")
                    planned.reps?.let { append(" × ${it.first}–${it.last} reps") }
                    planned.durationSeconds?.let { append(" × ${it}s") }
                    append(" · ${planned.restSeconds}s rest")
                }
                Text(detail, style = MaterialTheme.typography.bodySmall)

                if (planned.notes.isNotBlank()) {
                    Text("📝 ${planned.notes}", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (planned.exercise.instructions.isNotBlank()) {
                    Text(planned.exercise.instructions, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(
                when (planned.exercise.equipment) {
                    Equipment.NONE -> Icons.Default.AccessibilityNew
                    Equipment.BARBELL, Equipment.DUMBBELLS -> Icons.Default.FitnessCenter
                    Equipment.CARDIO_MACHINE -> Icons.Default.DirectionsRun
                    else -> Icons.Default.FitnessCenter
                },
                contentDescription = planned.exercise.equipment.label,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LogWorkoutDialog(
    day: TrainingDay,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var duration by remember { mutableStateOf("45") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(day.label, style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(duration.toIntOrNull() ?: 45, notes) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
