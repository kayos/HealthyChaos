package com.kayos.polar

import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.Flow

interface IRecordingsAPI {
    fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>>
    suspend fun startRecording()
    suspend fun stopRecording()
    suspend fun deleteRecording(entry: PolarOfflineRecordingEntry)
    suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): PolarOfflineRecordingData
    suspend fun isRecording(): Boolean
}
