package com.fitforge.app.presentation.checkin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.data.repository.CheckInRepository
import com.fitforge.app.domain.model.DailyCheckIn
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val repo: CheckInRepository
) : ViewModel() {
    fun save(checkIn: DailyCheckIn, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.logCheckIn(checkIn)
            onDone()
        }
    }
}

@Composable
fun CheckInScreen(
    onDone: () -> Unit,
    vm: CheckInViewModel = hiltViewModel()
) {
    var mental by remember { mutableIntStateOf(3) }
    var physical by remember { mutableIntStateOf(3) }
    var energy by remember { mutableIntStateOf(3) }
    var stress by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    val preview = DailyCheckIn(mentalState = mental, physicalState = physical,
        energyLevel = energy, stressLevel = stress)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Daily Check-in", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("How are you feeling today? This adjusts your workout recommendations.",
            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Readiness score preview
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Readiness Score", style = MaterialTheme.typography.labelMedium)
                    Text(preview.readinessLabel, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Text("${preview.readinessScore}/5", style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            }
        }

        RatingRow("Mental State", mental, listOf("Poor", "Low", "OK", "Good", "Great"),
            Icons.Default.Psychology) { mental = it }
        RatingRow("Physical State", physical, listOf("Very sore", "Tired", "OK", "Good", "Peak"),
            Icons.Default.DirectionsRun) { physical = it }
        RatingRow("Energy Level", energy, listOf("Drained", "Low", "Moderate", "High", "Max"),
            Icons.Default.BatteryChargingFull) { energy = it }
        RatingRow("Stress Level", stress, listOf("None", "Low", "Moderate", "High", "Max"),
            Icons.Default.Psychology) { stress = it }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { vm.save(DailyCheckIn(mentalState = mental, physicalState = physical,
                energyLevel = energy, stressLevel = stress, notes = notes), onDone) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Check-in") }
    }
}

@Composable
private fun RatingRow(
    label: String,
    value: Int,
    descriptions: List<String>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Text(descriptions.getOrNull(value - 1) ?: "", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { rating ->
                FilterChip(
                    selected = value == rating,
                    onClick = { onSelect(rating) },
                    label = { Text("$rating") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
