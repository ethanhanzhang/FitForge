package com.fitforge.app.presentation.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.domain.model.*

@Composable
fun WorkoutPlanScreen(
    onViewDetail: (String) -> Unit,
    vm: WorkoutViewModel = hiltViewModel()
) {
    val plan by vm.plan.collectAsState()

    if (plan == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val p = plan!!
    var selectedWeek by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(p.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(p.description, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GoalBadge(p.goal.label)
                GoalBadge("${p.durationWeeks} weeks")
                GoalBadge("${p.daysPerWeek}x/week")
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("Select Week", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            // Week selector tabs (scrollable row)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                p.weeks.take(4).forEachIndexed { idx, week ->
                    FilterChip(
                        selected = selectedWeek == idx,
                        onClick = { selectedWeek = idx },
                        label = { Text("Wk ${week.weekNumber}") }
                    )
                }
            }
        }

        item {
            Text(
                p.weeks[selectedWeek].focus,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        val days = p.weeks[selectedWeek].days
        items(days) { day ->
            TrainingDayCard(day = day, onClick = { onViewDetail(p.id) })
        }
    }
}

@Composable
fun TrainingDayCard(day: TrainingDay, onClick: () -> Unit) {
    val isRest = day.workoutType == WorkoutType.REST

    Card(
        onClick = { if (!isRest) onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isRest)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Day circle
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isRest) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.primary
            ) {
                Text(
                    "D${day.dayOfWeek}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isRest) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(day.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(
                    if (isRest) "Rest & Recover" else "${day.exercises.size} exercises · ${day.workoutType.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!isRest) Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GoalBadge(text: String) {
    Surface(shape = MaterialTheme.shapes.extraSmall, color = MaterialTheme.colorScheme.primaryContainer) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}
