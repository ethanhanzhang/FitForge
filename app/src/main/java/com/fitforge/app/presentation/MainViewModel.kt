package com.fitforge.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitforge.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _needsOnboarding = MutableStateFlow<Boolean?>(null)
    val needsOnboarding = _needsOnboarding.asStateFlow()

    init {
        viewModelScope.launch {
            _needsOnboarding.value = !userRepository.isOnboardingComplete()
        }
    }
}
