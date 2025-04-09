package com.kayos.healthykayos.sensor

import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.StateFlow

interface IHeartRateSensor {
    val heartRate: StateFlow<HeartRate?>

    fun search()
    fun connect(device: PolarDeviceInfo): Unit
    fun disconnect(id: String): Unit

    fun startHR(id: String): Flowable<PolarHrData>

    fun listRecordings(id: String)
    fun startRecording(deviceId: String): Completable
    fun stopRecording(id: String): Completable
    fun downloadRecording(deviceId: String, recording: PolarOfflineRecordingEntry): Single<PolarOfflineRecordingData>
    fun startHeartRateStream()
    fun stopHeartRateStream()
}
