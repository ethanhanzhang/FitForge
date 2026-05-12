package com.fitforge.app.presentation.sleep

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.data.repository.SleepRepository
import com.fitforge.app.domain.model.SleepLog
import com.fitforge.app.domain.model.RecoveryImpact
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(private val repo: SleepRepository) : ViewModel() {
    private val _recent = MutableStateFlow<List<SleepLog>>(emptyList())
    val recent = _recent.asStateFlow()

    init {
        viewModelScope.launch {
            repo.getRecent().collect { _recent.value = it }
        }
    }

    fun logSleep(log: SleepLog) { viewModelScope.launch { repo.logSleep(log) } }
}

@Composable
fun SleepScreen(vm: SleepViewModel = hiltViewModel()) {
    val recent by vm.recent.collectAsState()
    var hours by remember { mutableStateOf("8") }
    var quality by remember { mutableIntStateOf(3) }
    var bedTime by remember { mutableIntStateOf(22) }
    var wakeTime by remember { mutableIntStateOf(6) }
    var saved by remember { mutableStateOf(false) }

    val preview = SleepLog(durationHours = hours.toFloatOrNull() ?: 0f, quality = quality)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Sleep Tracker", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // Recovery impact card
        Card(colors = CardDefaults.cardColors(
            containerColor = when (preview.recoveryImpact) {
                RecoveryImpact.OPTIMAL -> MaterialTheme.colorScheme.primaryContainer
                RecoveryImpact.GOOD -> MaterialTheme.colorScheme.secondaryContainer
                RecoveryImpact.MODERATE -> MaterialTheme.colorScheme.tertiaryContainer
                RecoveryImpact.POOR -> MaterialTheme.colorScheme.errorContainer
            }
        )) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(32.dp))
                Column {
                    Text(preview.recoveryImpact.label, style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Text("Workout intensity modifier: ${(preview.recoveryImpact.intensityModifier * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Log form
        Card {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Log Last Night", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Sleep Duration (hours)") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Schedule, null) }
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sleep Quality", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { q ->
                            FilterChip(
                                selected = quality == q,
                                onClick = { quality = q },
                                label = { Text("$q") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    val labels = listOf("Very Poor", "Poor", "Fair", "Good", "Excellent")
                    Text(labels.getOrNull(quality - 1) ?: "", style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Bed Time", style = MaterialTheme.typography.labelMedium)
                        Slider(value = bedTime.toFloat(), onValueChange = { bedTime = it.toInt() },
                            valueRange = 18f..24f, steps = 5)
                        Text("${bedTime}:00", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Wake Time", style = MaterialTheme.typography.labelMedium)
                        Slider(value = wakeTime.toFloat(), onValueChange = { wakeTime = it.toInt() },
                            valueRange = 4f..10f, steps = 5)
                        Text("${wakeTime}:00", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Button(
                    onClick = {
                        vm.logSleep(SleepLog(durationHours = hours.toFloatOrNull() ?: 0f,
                            quality = quality, bedTimeHour = bedTime, wakeTimeHour = wakeTime))
                        saved = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Log Sleep") }

                if (saved) {
                    Text("Saved!", color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Recent history
        if (recent.isNotEmpty()) {
            Text("Recent Sleep", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            recent.take(7).forEach { log ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(log.date.toString(), style = MaterialTheme.typography.bodySmall)
                        Text("${log.durationHours}h", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold)
                        Text("Quality: ${log.quality}/5", style = MaterialTheme.typography.bodySmall)
                        Text(log.recoveryImpact.label, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
