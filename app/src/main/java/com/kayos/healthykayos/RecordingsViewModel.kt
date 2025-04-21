package com.kayos.healthykayos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.IHeartRateSensor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.await

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

    fun startRecording() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                sensor.startRecording().await()
                _recordingState.value = RecordingState.Recording()
            } catch (e: Exception) {
                //TODO #13 reasses assumption about recording status
                _recordingState.value = RecordingState.NotRecording()
                Log.e(TAG, "Problem starting recording ${e.message}")
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                sensor.stopRecording().await()
                _recordingState.value = RecordingState.NotRecording()
            } catch (e: Exception) {
                //TODO #13 reasses assumption about recording status
                _recordingState.value = RecordingState.Recording()
                Log.e(TAG, "Problem stopping recording ${e.message}")
            }
        }
    }

    companion object {
        private const val TAG = "RecordingsViewModel"
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