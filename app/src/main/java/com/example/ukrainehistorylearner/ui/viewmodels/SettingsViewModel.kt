package com.example.ukrainehistorylearner.ui.viewmodels

import android.content.Context
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ukrainehistorylearner.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
        viewModelScope.launch { store.setLanguage(code) }
    }

    fun setTheme(code: String) {
        viewModelScope.launch { store.setTheme(code) }
    }
}
