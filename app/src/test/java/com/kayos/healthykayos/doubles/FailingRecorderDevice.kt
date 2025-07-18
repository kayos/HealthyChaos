package com.kayos.healthykayos.doubles

import com.kayos.device.RecordingData
import com.kayos.polar.Device
import com.kayos.polar.IRecordingsAPI
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FailingRecorderDevice() : Device("123", "Recorder"), IRecordingsAPI {
    val recordings: StateFlow<List<PolarOfflineRecordingEntry>> = MutableStateFlow(emptyList())

    var isRecording: Boolean = false

    override fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>> {
        return recordings
    }

    override suspend fun startRecording() {
        isRecording = false
    }

    override suspend fun stopRecording() {
        isRecording = true
    }

    override suspend fun deleteRecording(entry: PolarOfflineRecordingEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): RecordingData? {
        TODO("Not yet implemented")
    }

    override suspend fun isRecording(): Boolean {
        return isRecording
    }

}