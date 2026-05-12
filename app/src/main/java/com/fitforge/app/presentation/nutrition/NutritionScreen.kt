package com.fitforge.app.presentation.nutrition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.data.local.entity.NutritionLogEntity
import com.fitforge.app.data.local.dao.NutritionDao
import com.fitforge.app.data.repository.UserRepository
import com.fitforge.app.domain.usecase.CalculateNutritionUseCase
import com.fitforge.app.domain.usecase.NutritionTargets
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val nutritionDao: NutritionDao,
    private val calcNutrition: CalculateNutritionUseCase
) : ViewModel() {

    private val _targets = MutableStateFlow<NutritionTargets?>(null)
    val targets = _targets.asStateFlow()

    val todayLogs: StateFlow<List<NutritionLogEntity>> = nutritionDao
        .getByDate(LocalDate.now().toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val profile = userRepository.getProfileOnce() ?: return@launch
            _targets.value = calcNutrition.execute(profile)
        }
    }

    fun addMeal(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        viewModelScope.launch {
            nutritionDao.insert(NutritionLogEntity(
                date = LocalDate.now().toString(),
                mealName = name, calories = calories,
                proteinG = protein, carbsG = carbs, fatG = fat, notes = ""
            ))
        }
    }

    fun deleteMeal(log: NutritionLogEntity) { viewModelScope.launch { nutritionDao.delete(log) } }
}

@Composable
fun NutritionScreen(vm: NutritionViewModel = hiltViewModel()) {
    val targets by vm.targets.collectAsState()
    val logs by vm.todayLogs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val totalCalories = logs.sumOf { it.calories }
    val totalProtein = logs.sumOf { it.proteinG.toDouble() }.toFloat()
    val totalCarbs = logs.sumOf { it.carbsG.toDouble() }.toFloat()
    val totalFat = logs.sumOf { it.fatG.toDouble() }.toFloat()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("Nutrition", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Add, "Add meal")
                }
            }
        }

        // Calorie progress
        targets?.let { t ->
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Calories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text("$totalCalories / ${t.calories} kcal", style = MaterialTheme.typography.bodyMedium)
                        }
                        LinearProgressIndicator(
                            progress = { (totalCalories.toFloat() / t.calories).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MacroProgress("Protein", totalProtein, t.proteinG.toFloat(), "g")
                            MacroProgress("Carbs", totalCarbs, t.carbsG.toFloat(), "g")
                            MacroProgress("Fat", totalFat, t.fatG.toFloat(), "g")
                        }
                        Text("💧 Water target: ${t.waterMl}ml", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            Text("Today's Meals", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        if (logs.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("No meals logged yet", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            TextButton(onClick = { showAddDialog = true }) { Text("Add your first meal") }
                        }
                    }
                }
            }
        }

        items(logs) { log ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(log.mealName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                        Text("P: ${log.proteinG}g · C: ${log.carbsG}g · F: ${log.fatG}g",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${log.calories} kcal", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    IconButton(onClick = { vm.deleteMeal(log) }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, "Delete", modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMealDialog(onDismiss = { showAddDialog = false }, onAdd = { name, cal, p, c, f ->
            vm.addMeal(name, cal, p, c, f)
            showAddDialog = false
        })
    }
}

@Composable
private fun MacroProgress(name: String, current: Float, target: Float, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("${"%.0f".format(current)}/${"%.0f".format(target)}$unit",
            style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        LinearProgressIndicator(progress = { (current / target).coerceIn(0f, 1f) },
            modifier = Modifier.width(80.dp))
        Text(name, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AddMealDialog(onDismiss: () -> Unit, onAdd: (String, Int, Float, Float, Float) -> Unit) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Meal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text("Meal name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = calories, onValueChange = { calories = it },
                    label = { Text("Calories (kcal)") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = protein, onValueChange = { protein = it },
                        label = { Text("Protein (g)") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = carbs, onValueChange = { carbs = it },
                        label = { Text("Carbs (g)") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                    OutlinedTextField(value = fat, onValueChange = { fat = it },
                        label = { Text("Fat (g)") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onAdd(name, calories.toIntOrNull() ?: 0, protein.toFloatOrNull() ?: 0f,
                    carbs.toFloatOrNull() ?: 0f, fat.toFloatOrNull() ?: 0f)
            }, enabled = name.isNotBlank() && calories.isNotBlank()) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
