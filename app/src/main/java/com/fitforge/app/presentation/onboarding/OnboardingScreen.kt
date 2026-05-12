package com.fitforge.app.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.domain.model.*

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    vm: OnboardingViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (state.step + 1) / 4f },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Step ${state.step + 1} of 4",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        AnimatedContent(targetState = state.step, label = "step") { step ->
            when (step) {
                0 -> StepBasics(state, vm)
                1 -> StepBodyMetrics(state, vm)
                2 -> StepGoal(state, vm)
                3 -> StepFitnessLevel(state, vm)
            }
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.step > 0) {
                OutlinedButton(onClick = vm::prevStep, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
            }

            Button(
                onClick = {
                    if (state.step < 3) vm.nextStep()
                    else vm.saveProfile(onComplete)
                },
                modifier = Modifier.weight(1f),
                enabled = !state.saving
            ) {
                if (state.saving) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text(if (state.step < 3) "Next" else "Get Started")
            }
        }
    }
}

@Composable
private fun StepBasics(state: OnboardingState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Welcome to FitForge", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Let's personalize your experience.", style = MaterialTheme.typography.bodyLarge)

        OutlinedTextField(
            value = state.name,
            onValueChange = { vm.update { copy(name = it) } },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = state.age,
            onValueChange = { vm.update { copy(age = it) } },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
        Text("Sex", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Sex.entries.forEach { sex ->
                FilterChip(
                    selected = state.sex == sex,
                    onClick = { vm.update { copy(sex = sex) } },
                    label = { Text(sex.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }
    }
}

@Composable
private fun StepBodyMetrics(state: OnboardingState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Your Body", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Used to calculate your calorie needs and customize training volume.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        OutlinedTextField(
            value = state.heightCm,
            onValueChange = { vm.update { copy(heightCm = it) } },
            label = { Text("Height (cm)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        OutlinedTextField(
            value = state.weightKg,
            onValueChange = { vm.update { copy(weightKg = it) } },
            label = { Text("Weight (kg)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Text("Activity Level", style = MaterialTheme.typography.labelLarge)
        ActivityLevel.entries.forEach { level ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(selected = state.activityLevel == level, onClick = { vm.update { copy(activityLevel = level) } })
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(level.label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun StepGoal(state: OnboardingState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Your Primary Goal", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        TrainingGoal.entries.forEach { goal ->
            val selected = state.goal == goal
            Card(
                onClick = { vm.update { copy(goal = goal) } },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(goal.icon, style = MaterialTheme.typography.titleLarge)
                    Column {
                        Text(goal.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(goal.description, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepFitnessLevel(state: OnboardingState, vm: OnboardingViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Fitness Level", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("This affects workout volume, intensity, and exercise selection.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        FitnessLevel.entries.forEach { level ->
            val selected = state.fitnessLevel == level
            Card(
                onClick = { vm.update { copy(fitnessLevel = level) } },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = selected, onClick = { vm.update { copy(fitnessLevel = level) } })
                    Spacer(Modifier.width(8.dp))
                    Text(level.label, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
