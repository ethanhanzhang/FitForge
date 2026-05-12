package com.fitforge.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.repository.UserRepository
import com.fitforge.app.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val step: Int = 0,                          // 0–3
    val name: String = "",
    val age: String = "",
    val sex: Sex = Sex.UNSPECIFIED,
    val heightCm: String = "",
    val weightKg: String = "",
    val goal: TrainingGoal = TrainingGoal.GENERAL_FITNESS,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER,
    val saving: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    fun update(transform: OnboardingState.() -> OnboardingState) = _state.update(transform)

    fun nextStep() = _state.update { it.copy(step = it.step + 1) }
    fun prevStep() = _state.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }

    fun saveProfile(onDone: () -> Unit) {
        val s = _state.value
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            userRepository.saveProfile(
                UserProfile(
                    name = s.name.trim(),
                    age = s.age.toIntOrNull() ?: 25,
                    sex = s.sex,
                    heightCm = s.heightCm.toFloatOrNull() ?: 170f,
                    weightKg = s.weightKg.toFloatOrNull() ?: 70f,
                    primaryGoal = s.goal,
                    activityLevel = s.activityLevel,
                    fitnessLevel = s.fitnessLevel
                )
            )
            onDone()
        }
    }
}
