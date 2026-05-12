package com.fitforge.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.repository.*
import com.fitforge.app.domain.model.*
import com.fitforge.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val profile: UserProfile? = null,
    val todaysCheckIn: DailyCheckIn? = null,
    val lastSleep: SleepLog? = null,
    val recommendedPlan: WorkoutPlan? = null,
    val intensityAdjustment: IntensityAdjustment? = null,
    val nutritionTargets: NutritionTargets? = null,
    val completedWorkouts: Int = 0,
    val loading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sleepRepository: SleepRepository,
    private val checkInRepository: CheckInRepository,
    private val workoutRepository: WorkoutRepository,
    private val recommendPlan: RecommendWorkoutPlanUseCase,
    private val calcNutrition: CalculateNutritionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = userRepository.getProfileOnce() ?: return@launch
            val sleep = sleepRepository.getTodaysSleep()
            val checkIn = checkInRepository.getTodaysCheckIn()
            val plan = recommendPlan.execute(profile)
            val adjustment = recommendPlan.adjustIntensity(plan, sleep, checkIn)
            val nutrition = calcNutrition.execute(profile)
            val completedWorkouts = workoutRepository.completedWorkoutCount()

            _state.value = DashboardUiState(
                profile = profile,
                todaysCheckIn = checkIn,
                lastSleep = sleep,
                recommendedPlan = plan,
                intensityAdjustment = adjustment,
                nutritionTargets = nutrition,
                completedWorkouts = completedWorkouts,
                loading = false
            )
        }
    }
}
