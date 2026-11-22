package com.example.watertrack.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.watertrack.R

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

enum class Tab(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    Dashboard("dashboard", R.string.tab_dashboard, Icons.Filled.Home),
    Goals("goals", R.string.tab_goals, Icons.Filled.TrackChanges), // أيقونة الهدف 🎯
    Tips("tips", R.string.tab_tips, Icons.Filled.Lightbulb),
    Account("account", R.string.tab_account, Icons.Filled.Person)
}