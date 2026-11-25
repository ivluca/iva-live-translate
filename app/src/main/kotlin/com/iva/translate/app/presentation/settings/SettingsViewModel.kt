package com.iva.translate.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.iva.translate.app.data.repository.UserPreferencesRepository
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("SettingsViewModel", "Unhandled coroutine error", throwable)
    }

    private val _userApiKey = MutableStateFlow("")
    val userApiKey: StateFlow<String> = _userApiKey.asStateFlow()

    private val _volumeRatio = MutableStateFlow(1.0f)
    val volumeRatio: StateFlow<Float> = _volumeRatio.asStateFlow()

    private val _originalVolume = MutableStateFlow(0.30f)
    val originalVolume: StateFlow<Float> = _originalVolume.asStateFlow()

    private val _voicePersona = MutableStateFlow("Zephyr")
    val voicePersona: StateFlow<String> = _voicePersona.asStateFlow()

    private val _targetLang = MutableStateFlow("vi")
    val targetLang: StateFlow<String> = _targetLang.asStateFlow()

    // Decoupled session state observed from DubbingSessionRepository
    val dubbingVolume: StateFlow<Float> = com.iva.translate.app.data.repository.DubbingSessionRepository.dubbingVolume
    val isDubbingMuted: StateFlow<Boolean> = com.iva.translate.app.data.repository.DubbingSessionRepository.isDubbingMuted
    val originalVolumeRatio: StateFlow<Float> = com.iva.translate.app.data.repository.DubbingSessionRepository.originalVolumeRatio
    val isOriginalMuted: StateFlow<Boolean> = com.iva.translate.app.data.repository.DubbingSessionRepository.isOriginalMuted

    private val _dubbingMode = MutableStateFlow(UserPreferencesRepository.MODE_SUBTITLE_ONLY)
    val dubbingMode: StateFlow<String> = _dubbingMode.asStateFlow()

    private val _showDualSubtitles = MutableStateFlow(true)
    val showDualSubtitles: StateFlow<Boolean> = _showDualSubtitles.asStateFlow()

    init {
        viewModelScope.launch(exceptionHandler) {
            try {
                _userApiKey.value = repository.apiKeyFlow.first()
                _volumeRatio.value = repository.volumeRatioFlow.first()
                _originalVolume.value = repository.originalVolumeFlow.first()
                _voicePersona.value = repository.voicePersonaFlow.first()
                _targetLang.value = repository.targetLangFlow.first()
                _dubbingMode.value = repository.dubbingModeFlow.first()
                _showDualSubtitles.value = repository.showDualSubtitlesFlow.first()
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to load preferences", e)
            }
        }
    }

    fun updateTargetLang(lang: String) {
        _targetLang.value = lang
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateTargetLang(lang)
                repository.updateTargetDialect("")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to update target language", e)
            }
        }
    }

    fun setDubbingVolume(vol: Float) {
        com.iva.translate.app.data.repository.DubbingSessionRepository.setDubbingVolume(vol)
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateVolumeRatio(vol)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to save dubbing volume", e)
            }
        }
    }

    fun toggleDubbingMute() {
        com.iva.translate.app.data.repository.DubbingSessionRepository.toggleDubbingMute()
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateDubbingMuted(com.iva.translate.app.data.repository.DubbingSessionRepository.isDubbingMuted.value)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to save dubbing mute", e)
            }
        }
    }

    fun setOriginalVolumeRatio(ratio: Float) {
        com.iva.translate.app.data.repository.DubbingSessionRepository.setOriginalVolumeRatio(ratio)
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateOriginalVolume(ratio)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to save original volume", e)
            }
        }
    }

    fun toggleOriginalMute() {
        com.iva.translate.app.data.repository.DubbingSessionRepository.toggleOriginalMute()
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateOriginalMuted(com.iva.translate.app.data.repository.DubbingSessionRepository.isOriginalMuted.value)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to save original mute", e)
            }
        }
    }

    fun updateApiKey(key: String) {
        _userApiKey.value = key
        viewModelScope.launch(exceptionHandler) {
            repository.updateApiKey(key)
        }
    }

    fun updateVoicePersona(voice: String) {
        _voicePersona.value = voice
        viewModelScope.launch(exceptionHandler) {
            repository.updateVoicePersona(voice)
        }
    }

    fun updateDubbingMode(mode: String) {
        _dubbingMode.value = mode
        viewModelScope.launch(exceptionHandler) {
            repository.updateDubbingMode(mode)
        }
    }

    fun updateShowDualSubtitles(show: Boolean) {
        _showDualSubtitles.value = show
        viewModelScope.launch(exceptionHandler) {
            repository.updateShowDualSubtitles(show)
            com.iva.translate.app.data.repository.DubbingSessionRepository.setShowDualSubtitles(show)
        }
    }

    fun saveSettings() {
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.updateApiKey(_userApiKey.value)
                repository.updateVoicePersona(_voicePersona.value)
                repository.updateTargetLang(_targetLang.value)
                repository.updateDubbingMode(_dubbingMode.value)
                repository.updateShowDualSubtitles(_showDualSubtitles.value)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to save preferences", e)
            }
        }
    }
}

class SettingsViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
