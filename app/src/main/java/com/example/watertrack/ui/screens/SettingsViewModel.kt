package com.example.watertrack.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.watertrack.WaterTrackApp
import com.example.watertrack.data.AppPreferences
import com.example.watertrack.util.Security
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val app: WaterTrackApp,
    private val prefs: AppPreferences
) : ViewModel() {

    val reminderStart: StateFlow<Int> = prefs.reminderStart
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    val reminderEnd: StateFlow<Int> = prefs.reminderEnd
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 22)

    val language: StateFlow<String> = prefs.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "fr")

    val darkMode: StateFlow<Boolean> = prefs.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setWindow(start: Int, end: Int) {
        viewModelScope.launch { prefs.setReminderWindow(start, end) }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { prefs.setLanguage(code) }
    }

    fun setDarkMode(on: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(on) }
    }

    /** Retourne true si le mot de passe a ete change */
    fun changePassword(oldPassword: String, newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val id = prefs.userId.first()
            val user = app.repository.observeUser(id).first()
            if (user == null || user.passwordHash != Security.sha256(oldPassword)) {
                onResult(false)
                return@launch
            }
            app.repository.updateUser(user.copy(passwordHash = Security.sha256(newPassword)))
            onResult(true)
        }
    }
}

class SettingsViewModelFactory(private val app: WaterTrackApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SettingsViewModel(app, AppPreferences(app)) as T
}