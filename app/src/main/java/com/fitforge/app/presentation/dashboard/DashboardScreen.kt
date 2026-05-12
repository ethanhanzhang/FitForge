package com.fitforge.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.ui.theme.SuccessGreen
import com.fitforge.app.ui.theme.WarningAmber
import com.fitforge.app.ui.theme.ErrorRed
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    onGoToWorkout: () -> Unit,
    onGoToCheckIn: () -> Unit,
    vm: DashboardViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    "Good ${greeting()}, ${state.profile?.name ?: "Athlete"}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.AccountCircle, contentDescription = null,
                modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
        }

        // Readiness card
        state.intensityAdjustment?.let { adj ->
            val (cardColor, icon) = when {
                adj.modifier < 0.3f -> Pair(ErrorRed.copy(alpha = 0.15f), Icons.Default.Hotel)
                adj.modifier < 0.7f -> Pair(WarningAmber.copy(alpha = 0.15f), Icons.Default.Warning)
                else -> Pair(SuccessGreen.copy(alpha = 0.15f), Icons.Default.CheckCircle)
            }
            Card(colors = CardDefaults.cardColors(containerColor = cardColor)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
                    Column {
                        Text("Today's Readiness", style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(adj.recommendation, style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium)
                        if (state.todaysCheckIn == null) {
                            TextButton(onClick = onGoToCheckIn, contentPadding = PaddingValues(0.dp)) {
                                Text("Log today's check-in →", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        // Stats row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.FitnessCenter,
                value = "${state.completedWorkouts}",
                label = "Workouts Done"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Bedtime,
                value = state.lastSleep?.let { "${it.durationHours}h" } ?: "--",
                label = "Last Sleep"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocalFire,
                value = state.nutritionTargets?.let { "${it.calories}" } ?: "--",
                label = "Kcal Target"
            )
        }

        // Recommended plan card
        state.recommendedPlan?.let { plan ->
            Card(
                onClick = onGoToWorkout,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Text("Your Plan", style = MaterialTheme.typography.labelMedium)
                    }
                    Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(plan.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        PlanChip("${plan.durationWeeks} weeks")
                        PlanChip("${plan.daysPerWeek}x / week")
                        PlanChip(plan.goal.label)
                    }
                }
            }
        }

        // Nutrition summary
        state.nutritionTargets?.let { targets ->
            Card {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Nutrition Targets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MacroItem("Calories", "${targets.calories}", "kcal")
                        MacroItem("Protein", "${targets.proteinG}g", "${(targets.proteinCalories * 100 / targets.calories)}%")
                        MacroItem("Carbs", "${targets.carbsG}g", "${(targets.carbCalories * 100 / targets.calories)}%")
                        MacroItem("Fat", "${targets.fatG}g", "${(targets.fatCalories * 100 / targets.calories)}%")
                    }
                    Text("💧 Water: ${targets.waterMl}ml / day", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Profile snapshot
        state.profile?.let { profile ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Profile", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("BMI: ${"%.1f".format(profile.bmi)}", style = MaterialTheme.typography.bodySmall)
                        Text("TDEE: ${profile.tdee.toInt()} kcal", style = MaterialTheme.typography.bodySmall)
                        Text(profile.fitnessLevel.label, style = MaterialTheme.typography.bodySmall)
                        Text(profile.activityLevel.label, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, icon: ImageVector, value: String, label: String) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PlanChip(text: String) {
    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
        Text(text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun MacroItem(name: String, value: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun greeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when {
        hour < 12 -> "Morning"
        hour < 17 -> "Afternoon"
        else -> "Evening"
    }
}
