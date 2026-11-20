package com.example.watertrack.ui.screens

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.watertrack.WaterTrackApp
import com.example.watertrack.data.AppPreferences
import com.example.watertrack.data.AuthResult
import com.example.watertrack.data.Repository
import com.example.watertrack.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    private val app: WaterTrackApp,
    private val repo: Repository,
    private val prefs: AppPreferences
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)

    fun login(identifier: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            loading = true
            error = null
            when (val r = repo.login(identifier.trim(), password)) {
                is AuthResult.Success -> {
                    prefs.setUserId(r.userId)
                    onSuccess()
                }
                else -> error = "Identifiant ou mot de passe incorrect"
            }
            loading = false
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        birthDate: Long,
        photoUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            loading = true
            error = null
            when (val r = repo.register(firstName, lastName, email, phone, password, birthDate)) {
                is AuthResult.Success -> {
                    if (photoUri != null) {
                        val path = withContext(Dispatchers.IO) {
                            ImageUtils.saveProfilePhoto(app, photoUri, r.userId)
                        }
                        if (path != null) repo.setPhoto(r.userId, path)
                    }
                    prefs.setUserId(r.userId)
                    onSuccess()
                }
                is AuthResult.EmailExists -> error = "Cet email est déjà utilisé"
                is AuthResult.PhoneExists -> error = "Ce numéro est déjà utilisé"
                else -> error = "Erreur lors de l'inscription"
            }
            loading = false
        }
    }
}

class AuthViewModelFactory(private val app: WaterTrackApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AuthViewModel(app, app.repository, AppPreferences(app)) as T
}