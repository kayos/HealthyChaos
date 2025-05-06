package com.kayos.healthykayos.doubles

import com.kayos.polar.HeartRate
import com.kayos.polar.IHeartRateSensor
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FailingSensorStub() : IHeartRateSensor {
    override val heartRate: StateFlow<HeartRate?> = MutableStateFlow(null)
    override val availableDevices: StateFlow<List<PolarDeviceInfo>> = MutableStateFlow(emptyList())
    override val connectedDevices: StateFlow<PolarDeviceInfo?> = MutableStateFlow(null)

    val recordings: StateFlow<List<PolarOfflineRecordingEntry>> = MutableStateFlow(emptyList())

    var isRecording: Boolean = false

    override fun search() {
        TODO("Not yet implemented")
    }

    override fun connect(deviceId: String) {
        TODO("Not yet implemented")
    }

    override fun disconnect(id: String) {
        TODO("Not yet implemented")
    }

    override fun startHR(id: String): Flowable<PolarHrData> {
        TODO("Not yet implemented")
    }

    override fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>> {
        return recordings
    }

    override fun startRecording(): Completable {
        isRecording = false
        return Completable.error(Throwable())
    }

    override fun stopRecording(): Completable {
        isRecording = true //assume failed to stop
        return Completable.error(Throwable())
    }

    override suspend fun deleteRecording(entry: PolarOfflineRecordingEntry) {
        TODO("Not yet implemented")
    }

    override suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): PolarOfflineRecordingData {
        TODO("Not yet implemented")
    }

    override suspend fun isRecording(): Boolean {
        return isRecording
    }

    override fun startHeartRateStream() {
        TODO("Not yet implemented")
    }

    override fun stopHeartRateStream() {
        TODO("Not yet implemented")
    }

}