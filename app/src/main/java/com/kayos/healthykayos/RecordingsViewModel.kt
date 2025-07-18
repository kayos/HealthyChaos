package com.kayos.healthykayos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kayos.device.RecordingData
import com.kayos.polar.DeviceManager
import com.kayos.polar.IRecordingsAPI
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.Writer

data class RecordingsUiState(
    val recordingStatus: RecordingState,
    val recordings: List<PolarOfflineRecordingEntry>)

class RecordingsViewModel(deviceManager: DeviceManager = DeviceManager.getInstance()) : ViewModel(){
    private val _recordingState : MutableStateFlow<RecordingState> = MutableStateFlow(RecordingState.NotRecording())
    private val _refreshRecordings : MutableStateFlow<Int> = MutableStateFlow(0)
    private val _connectedRecorder : StateFlow<IRecordingsAPI?> =
        deviceManager.connectedDevice.map {
            device -> device?.getRecordingsFunctionality()}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _recordings: Flow<List<PolarOfflineRecordingEntry>> = combine(
        _refreshRecordings,
        _connectedRecorder.filterNotNull()){ _, device -> device }
        .flatMapLatest { device -> device.listRecordings() }
        .map { recordings -> recordings.sortedBy { entry -> entry.date } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList<PolarOfflineRecordingEntry>(),
        )

    val uiState: StateFlow<RecordingsUiState> = combine(
        _recordings,
        _recordingState)
        { recordings, status -> RecordingsUiState(status, recordings) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = RecordingsUiState(RecordingState.NotRecording(), emptyList()),
        )

    init {
        viewModelScope.launch {
            determineRecordingState()
        }
    }

    fun startRecording() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _connectedRecorder.value?.startRecording()
            } catch (e: Exception) {
                Log.e(TAG, "Problem starting recording ${e.message}")
            }
            finally {
                determineRecordingState()
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                _connectedRecorder.value?.stopRecording()
            } catch (e: Exception) {
                Log.e(TAG, "Problem stopping recording ${e.message}")
            }
            finally {
                determineRecordingState()
            }
        }
    }

    fun download(recording: PolarOfflineRecordingEntry, writer: Writer) {
        viewModelScope.launch(Dispatchers.Main) {
            val data = _connectedRecorder.value?.downloadRecording(recording)
            when (data) {
                is RecordingData.HeartRateRecording -> {
                    saveDataToCSV(data, writer)
                }
                else -> {
                    Log.d("RecordingsFragment", "Recording type is not yet implemented")
                }
            }
        }
    }

    // TODO refactor into separate Export class
    fun saveDataToCSV(data : RecordingData.HeartRateRecording, writer: Writer) {
        try {
            writer.write("time,bpm")
            writer.write("\n")

            for (sample in data.data.samples) {
                writer.write("${sample.secondsFromStart},${sample.bpm}")
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
            try {
                _connectedRecorder.value?.deleteRecording(recording)
            } catch (e: Exception) {
                Log.e(TAG, "Problem deleting recording ${e.message}")
            }
            finally {
                refreshRecordings()
            }
        }
    }

    fun refreshRecordings() {
        _refreshRecordings.tryEmit(_refreshRecordings.value+1)
    }

    private suspend fun determineRecordingState() {
        var isRecording = _connectedRecorder.value?.isRecording()
        if (isRecording != null) {
            _recordingState.value = RecordingState.determineState(isRecording)
        }
    }

    companion object {
        private const val TAG = "RecordingsViewModel"
    }

}