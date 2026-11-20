package com.example.watertrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.watertrack.WaterTrackApp
import com.example.watertrack.data.AppPreferences
import com.example.watertrack.ui.screens.AuthViewModel
import com.example.watertrack.ui.screens.AuthViewModelFactory
import com.example.watertrack.ui.screens.LoginScreen
import com.example.watertrack.ui.screens.MainScreen
import com.example.watertrack.ui.screens.OnboardingScreen
import com.example.watertrack.ui.screens.RegisterScreen
import com.example.watertrack.ui.screens.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * true  = l'Onboarding s'affiche a chaque lancement (pour tester le design)
 * false = l'Onboarding s'affiche une seule fois, au premier lancement (normal)
 */
private const val ALWAYS_SHOW_ONBOARDING = false

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as WaterTrackApp
    val prefs = AppPreferences(context)
    val scope = rememberCoroutineScope()

    NavHost(navController = nav, startDestination = Routes.SPLASH) {

        // 1) Splash
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                scope.launch {
                    val done = prefs.onboardingDone.first()
                    val userId = prefs.userId.first()
                    val next = when {
                        ALWAYS_SHOW_ONBOARDING || !done -> Routes.ONBOARDING
                        userId == -1L -> Routes.LOGIN
                        else -> Routes.MAIN
                    }
                    nav.navigate(next) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            })
        }

        // 2) Onboarding (premier lancement uniquement)
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinished = {
                scope.launch {
                    prefs.setOnboardingDone()
                    val userId = prefs.userId.first()
                    val next = if (userId == -1L) Routes.LOGIN else Routes.MAIN
                    nav.navigate(next) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            })
        }

        // 3) Connexion
        composable(Routes.LOGIN) {
            val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(app))
            LoginScreen(
                vm = vm,
                onLoggedIn = {
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegister = { nav.navigate(Routes.REGISTER) }
            )
        }

        // 4) Creation de compte
        composable(Routes.REGISTER) {
            val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(app))
            RegisterScreen(
                vm = vm,
                onRegistered = {
                    nav.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = { nav.popBackStack() }
            )
        }

        // 5) Ecran principal (onglets)
        composable(Routes.MAIN) { MainScreen() }
    }
}