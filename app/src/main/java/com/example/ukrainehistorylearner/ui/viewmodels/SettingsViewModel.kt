package com.example.ukrainehistorylearner.ui.viewmodels

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ukrainehistorylearner.MainActivity
import com.example.ukrainehistorylearner.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val store = SettingsDataStore(application)

    val notifications: StateFlow<Boolean> = store.notificationsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val sound: StateFlow<Boolean> = store.soundFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val language: StateFlow<String> = store.languageFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "uk")

    val theme: StateFlow<String> = store.themeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "system")

    fun toggleNotifications() {
        viewModelScope.launch { store.setNotifications(!notifications.value) }
    }

    fun toggleSound() {
        viewModelScope.launch { store.setSound(!sound.value) }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch {
            store.setLanguage(code)
            updateAppLocale(code)

            val context = getApplication<Application>().applicationContext
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    fun setTheme(code: String) {
        viewModelScope.launch { store.setTheme(code) }
    }

    private fun updateAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val context = getApplication<Application>()
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun applySavedLanguage() {
        viewModelScope.launch {
            language.collect { langCode ->
                updateAppLocale(langCode)
            }
        }
    }
}