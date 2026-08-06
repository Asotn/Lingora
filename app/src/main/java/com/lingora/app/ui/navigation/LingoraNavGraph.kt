package com.lingora.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lingora.app.LingoraApplication
import com.lingora.app.di.AppContainer
import com.lingora.app.ui.home.HomeScreen
import com.lingora.app.ui.home.HomeViewModel
import com.lingora.app.ui.onboarding.OnboardingLevelScreen
import com.lingora.app.ui.onboarding.OnboardingPurposeScreen
import com.lingora.app.ui.onboarding.OnboardingViewModel
import com.lingora.app.ui.settings.SettingsScreen
import com.lingora.app.ui.settings.SettingsViewModel

/** Route names, shared between this graph and [com.lingora.app.MainActivity]
 *  (which needs to know the onboarding/home split to pick a start route). */
object Routes {
    const val PURPOSE = "onboarding/purpose"
    const val LEVEL = "onboarding/level"
    const val HOME = "home"
    const val SETTINGS = "settings"
}

@Composable
fun LingoraNavGraph(startDestination: String) {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as LingoraApplication
    val container = application.container

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.PURPOSE) {
            val viewModel: OnboardingViewModel = viewModel(factory = onboardingFactory(container))
            OnboardingPurposeScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Routes.LEVEL) }
            )
        }
        composable(Routes.LEVEL) {
            // Reuse the same OnboardingViewModel instance across both
            // onboarding screens by scoping it to the first screen's
            // back-stack entry instead of this one.
            val parentEntry = remember(navController) { navController.getBackStackEntry(Routes.PURPOSE) }
            val viewModel: OnboardingViewModel = viewModel(parentEntry, factory = onboardingFactory(container))
            OnboardingLevelScreen(
                viewModel = viewModel,
                onFinished = { navController.navigate(Routes.HOME) { popUpTo(0) } }
            )
        }
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel(factory = homeFactory(container))
            HomeScreen(
                viewModel = viewModel,
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(Routes.SETTINGS) {
            val viewModel: SettingsViewModel = viewModel(factory = settingsFactory(container))
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRestartOnboarding = { navController.navigate(Routes.PURPOSE) { popUpTo(0) } }
            )
        }
    }
}

private fun onboardingFactory(container: AppContainer) = viewModelFactory {
    initializer { OnboardingViewModel(container.preferences) }
}

private fun homeFactory(container: AppContainer) = viewModelFactory {
    initializer { HomeViewModel(container.translationRepository, container.preferences, container.ttsManager) }
}

private fun settingsFactory(container: AppContainer) = viewModelFactory {
    initializer { SettingsViewModel(container.preferences, container.ttsManager) }
}
