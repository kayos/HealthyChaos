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
import kotlinx.coroutines.flow.flowOf

class SensorStub(override val heartRate: StateFlow<HeartRate?>) : IHeartRateSensor {
    var isRecording: Boolean = false

    override fun search() {
        TODO("Not yet implemented")
    }

    override fun connect(device: PolarDeviceInfo) {
        TODO("Not yet implemented")
    }

    override fun disconnect(id: String) {
        TODO("Not yet implemented")
    }

    override fun startHR(id: String): Flowable<PolarHrData> {
        TODO("Not yet implemented")
    }

    override fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>> {
        return flowOf(emptyList<PolarOfflineRecordingEntry>())
    }

    override fun startRecording(): Completable {
        isRecording = true
        return Completable.complete()
    }

    override fun stopRecording(): Completable {
        isRecording = false
        return Completable.complete()
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