package com.kayos.healthykayos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.polar.IHeartRateSensor
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.await
import java.io.IOException
import java.io.Writer
import java.util.Calendar

class RecordingsViewModel(val sensor: IHeartRateSensor) : ViewModel(){
    private val _recordingState : MutableStateFlow<RecordingState> = MutableStateFlow(RecordingState.NotRecording())
    val recordingState: StateFlow<RecordingState> get() = _recordingState

    private val _refresh : MutableStateFlow<Int> = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val recordings: Flow<List<PolarOfflineRecordingEntry>> = _refresh.flatMapLatest { _ ->
            sensor.listRecordings()
        }
        .map { recordings -> recordings.sortedBy { entry -> entry.date } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList<PolarOfflineRecordingEntry>(),
        )

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
            } catch (e: Exception) {
                Log.e(TAG, "Problem starting recording ${e.message}")
            }
            finally {
                _recordingState.value = RecordingState.determineState(sensor.isRecording())
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                sensor.stopRecording().await()
            } catch (e: Exception) {
                Log.e(TAG, "Problem stopping recording ${e.message}")
            }
            finally {
                _recordingState.value = RecordingState.determineState(sensor.isRecording())
            }
        }
    }

    fun download(recording: PolarOfflineRecordingEntry, writer: Writer) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = sensor.downloadRecording(recording)
            when (data) {
                is PolarOfflineRecordingData.HrOfflineRecording -> {
                    saveDataToCSV(data, writer)
                }
                else -> {
                    Log.d("RecordingsFragment", "Recording type is not yet implemented")
                }
            }
        }
    }

    // TODO refactor into separate Export class
    fun saveDataToCSV(data : PolarOfflineRecordingData.HrOfflineRecording, writer: Writer) {
        try {
            writer.write("time,hr,correctedHr")
            writer.write("\n")

            var timestamp = data.startTime
            for (sample in data.data.samples) {
                timestamp.add(Calendar.SECOND, 1)
                writer.write("${timestamp.time},${sample.hr},${sample.correctedHr}")
                writer.write("\n")
            }
            writer.flush()
            writer.close()
            Log.d(TAG, "Done saving to file")
        } catch (e: IOException) {
            Log.e(TAG, "Error saving. ${e.message}")
        }
    }

    fun deleteRecording(recording: PolarOfflineRecordingEntry) {
        viewModelScope.launch(Dispatchers.Main) {
            sensor.deleteRecording(recording)
        }
    }

    fun refresh() {
        _refresh.tryEmit(_refresh.value+1)
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