package com.kayos.healthykayos.doubles

import com.kayos.healthykayos.sensor.HeartRate
import com.kayos.healthykayos.sensor.IHeartRateSensor
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.StateFlow

class SensorStub(override val heartRate: StateFlow<HeartRate?>,
                 override val recordings: StateFlow<List<PolarOfflineRecordingEntry>>
) : IHeartRateSensor {
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

    override fun listRecordings() {
        TODO("Not yet implemented")
    }

    override fun startRecording(): Completable {
        return Completable.complete()
    }

    override fun stopRecording(): Completable {
        TODO("Not yet implemented")
    }

    override fun deleteRecording(entry: PolarOfflineRecordingEntry) {
        TODO("Not yet implemented")
    }

    override fun downloadRecording(recording: PolarOfflineRecordingEntry): Single<PolarOfflineRecordingData> {
        TODO("Not yet implemented")
    }

    override suspend fun isRecording(): Boolean {
        TODO("Not yet implemented")
    }


    override fun startHeartRateStream() {
        TODO("Not yet implemented")
    }

    override fun stopHeartRateStream() {
        TODO("Not yet implemented")
    }

}