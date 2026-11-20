package com.example.watertrack.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {
    private val onboardingKey = booleanPreferencesKey("onboarding_done")
    private val userIdKey = longPreferencesKey("user_id")
    private val cupKey = intPreferencesKey("cup_ml")

    val onboardingDone: Flow<Boolean> =
        context.dataStore.data.map { it[onboardingKey] ?: false }

    val userId: Flow<Long> =
        context.dataStore.data.map { it[userIdKey] ?: -1L }

    val cupMl: Flow<Int> =
        context.dataStore.data.map { it[cupKey] ?: 250 }

    suspend fun setOnboardingDone() {
        context.dataStore.edit { it[onboardingKey] = true }
    }

    suspend fun setUserId(id: Long) {
        context.dataStore.edit { it[userIdKey] = id }
    }

    suspend fun setCupMl(ml: Int) {
        context.dataStore.edit { it[cupKey] = ml }
    }
}