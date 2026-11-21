package com.example.watertrack.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {
    private val onboardingKey = booleanPreferencesKey("onboarding_done")
    private val userIdKey = longPreferencesKey("user_id")
    private val cupKey = intPreferencesKey("cup_ml")
    private val favoritesKey = stringSetPreferencesKey("favorite_tips")
    private val reminderKey = intPreferencesKey("reminder_hours")
    private val startKey = intPreferencesKey("reminder_start")
    private val endKey = intPreferencesKey("reminder_end")
    private val langKey = stringPreferencesKey("language")
    private val darkKey = booleanPreferencesKey("dark_mode")

    private fun coverKey(userId: Long) = stringPreferencesKey("cover_$userId")

    val onboardingDone: Flow<Boolean> =
        context.dataStore.data.map { it[onboardingKey] ?: false }

    val userId: Flow<Long> =
        context.dataStore.data.map { it[userIdKey] ?: -1L }

    val cupMl: Flow<Int> =
        context.dataStore.data.map { it[cupKey] ?: 250 }

    val favoriteTips: Flow<Set<String>> =
        context.dataStore.data.map { it[favoritesKey] ?: emptySet() }

    val reminderHours: Flow<Int> =
        context.dataStore.data.map { it[reminderKey] ?: 2 }

    val reminderStart: Flow<Int> =
        context.dataStore.data.map { it[startKey] ?: 8 }

    val reminderEnd: Flow<Int> =
        context.dataStore.data.map { it[endKey] ?: 22 }

    val language: Flow<String> =
        context.dataStore.data.map { it[langKey] ?: "fr" }

    val darkMode: Flow<Boolean> =
        context.dataStore.data.map { it[darkKey] ?: false }

    fun coverPath(userId: Long): Flow<String?> =
        context.dataStore.data.map { it[coverKey(userId)] }

    suspend fun setOnboardingDone() {
        context.dataStore.edit { it[onboardingKey] = true }
    }

    suspend fun setUserId(id: Long) {
        context.dataStore.edit { it[userIdKey] = id }
    }

    suspend fun setCupMl(ml: Int) {
        context.dataStore.edit { it[cupKey] = ml }
    }

    suspend fun toggleFavorite(tipId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey] ?: emptySet()
            prefs[favoritesKey] = if (tipId in current) current - tipId else current + tipId
        }
    }

    suspend fun setReminderHours(hours: Int) {
        context.dataStore.edit { it[reminderKey] = hours }
    }

    suspend fun setReminderWindow(start: Int, end: Int) {
        context.dataStore.edit {
            it[startKey] = start
            it[endKey] = end
        }
    }

    suspend fun setLanguage(code: String) {
        context.dataStore.edit { it[langKey] = code }
    }

    suspend fun setDarkMode(on: Boolean) {
        context.dataStore.edit { it[darkKey] = on }
    }

    suspend fun setCoverPath(userId: Long, path: String?) {
        context.dataStore.edit { prefs ->
            if (path == null) prefs.remove(coverKey(userId)) else prefs[coverKey(userId)] = path
        }
    }
}