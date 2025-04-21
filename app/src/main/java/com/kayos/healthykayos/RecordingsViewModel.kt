package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.IHeartRateSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecordingsViewModel(val sensor: IHeartRateSensor) : ViewModel(){
    val _recordingState : MutableStateFlow<RecordingState> = MutableStateFlow(RecordingState.NotRecording())
    val recordingState: StateFlow<RecordingState> get() = _recordingState

    init {
        viewModelScope.launch {
            _recordingState.value = when (sensor.isRecording()){
                true -> RecordingState.Recording()
                false -> RecordingState.NotRecording()
            }

        }
    }

    // Temporary for refactor
    fun setRecording(){
        _recordingState.value = RecordingState.Recording()
    }

    fun setNotRecording(){
        _recordingState.value = RecordingState.NotRecording()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                RecordingsViewModel(
                    sensor = HeartRateProviderFactory.getPolarHeartRateSensor(application.applicationContext)
                )
            }
        }
    }

}