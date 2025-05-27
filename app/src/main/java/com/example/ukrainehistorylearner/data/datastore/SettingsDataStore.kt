package com.example.ukrainehistorylearner.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val NOTIFICATIONS = booleanPreferencesKey("notifications")
    val SOUND = booleanPreferencesKey("sound")
    val LANGUAGE = stringPreferencesKey("language")
    val THEME = stringPreferencesKey("theme")
}

class SettingsDataStore(private val context: Context) {

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { it[SettingsKeys.NOTIFICATIONS] ?: true }

    val soundFlow: Flow<Boolean> = context.dataStore.data
        .map { it[SettingsKeys.SOUND] ?: true }

    val languageFlow: Flow<String> = context.dataStore.data
        .map { it[SettingsKeys.LANGUAGE] ?: "uk" }

    val themeFlow: Flow<String> = context.dataStore.data
        .map { it[SettingsKeys.THEME] ?: "system" }

    suspend fun setNotifications(value: Boolean) {
        context.dataStore.edit { it[SettingsKeys.NOTIFICATIONS] = value }
    }

    suspend fun setSound(value: Boolean) {
        context.dataStore.edit { it[SettingsKeys.SOUND] = value }
    }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[SettingsKeys.LANGUAGE] = value }
    }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[SettingsKeys.THEME] = value }
    }
}
