package com.lingora.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lingora.app.ui.navigation.LingoraNavGraph
import com.lingora.app.ui.navigation.Routes
import com.lingora.app.ui.theme.LingoraTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val container = (application as LingoraApplication).container
            var startDestination by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                val onboardingDone = container.preferences.isOnboardingComplete.first()
                startDestination = if (onboardingDone) Routes.HOME else Routes.PURPOSE
            }

            LingoraTheme {
                startDestination?.let { destination ->
                    LingoraNavGraph(startDestination = destination)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (application as LingoraApplication).container.ttsManager.release()
    }
}
