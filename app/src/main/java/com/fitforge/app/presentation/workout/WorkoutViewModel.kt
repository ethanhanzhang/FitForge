package com.fitforge.app.presentation.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.repository.UserRepository
import com.fitforge.app.data.repository.WorkoutRepository
import com.fitforge.app.domain.model.*
import com.fitforge.app.domain.usecase.RecommendWorkoutPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val recommendPlan: RecommendWorkoutPlanUseCase
) : ViewModel() {

    private val _plan = MutableStateFlow<WorkoutPlan?>(null)
    val plan = _plan.asStateFlow()

    private val _logSuccess = MutableStateFlow(false)
    val logSuccess = _logSuccess.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = userRepository.getProfileOnce() ?: return@launch
            _plan.value = recommendPlan.execute(profile)
        }
    }

    fun logWorkout(day: TrainingDay, durationMinutes: Int, notes: String) {
        viewModelScope.launch {
            workoutRepository.logSession(
                planId = _plan.value?.id ?: "unknown",
                dayLabel = day.label,
                workoutType = day.workoutType.name,
                durationMinutes = durationMinutes,
                notes = notes
            )
            _logSuccess.value = true
        }
    }

    fun resetLogSuccess() { _logSuccess.value = false }
}
