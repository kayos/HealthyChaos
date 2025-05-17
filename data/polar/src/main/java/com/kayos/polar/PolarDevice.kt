package com.kayos.polar

import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx3.await

class PolarDevice(id: String, name: String, val api: PolarBleApi): Device(id, name), IRecordingsAPI{

    override fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>> {
        val recordings = mutableListOf<PolarOfflineRecordingEntry>()
        return api.listOfflineRecordings(id).map { entry ->
            recordings.add(entry)
            recordings.toList()
        }.asFlow()
    }

    override suspend fun startRecording() {
        api.startOfflineRecording(id, PolarBleApi.PolarDeviceDataType.HR).await()

    }

    override suspend fun stopRecording() {
        api.stopRecording(id).await()
    }

    override suspend fun deleteRecording(recording: PolarOfflineRecordingEntry) {
        api.removeOfflineRecord(id, recording).await()
    }

    override suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): PolarOfflineRecordingData {
        return api.getOfflineRecord(id, recording).await()
    }

    override suspend fun isRecording(): Boolean {
        return api.getOfflineRecordingStatus(id).map { runningRecordings ->
            runningRecordings.contains(PolarBleApi.PolarDeviceDataType.HR)
        }.await()
    }

}