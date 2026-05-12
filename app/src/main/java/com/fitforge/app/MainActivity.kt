package com.fitforge.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.fitforge.app.navigation.AppNavigation
import com.fitforge.app.presentation.MainViewModel
import com.fitforge.app.ui.theme.FitForgeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitForgeTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val needsOnboarding by viewModel.needsOnboarding.collectAsState()

                if (needsOnboarding != null) {
                    AppNavigation(startOnboarding = needsOnboarding!!)
                }
            }
        }
    }
}
