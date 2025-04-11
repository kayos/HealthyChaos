package com.kayos.healthykayos.sensor

import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.StateFlow

// TODO: remove any reference to Polar stuff to make this a more generic interface
interface IHeartRateSensor {
    val heartRate: StateFlow<HeartRate?>
    val recordings: StateFlow<List<PolarOfflineRecordingEntry>>

    fun search()
    fun connect(device: PolarDeviceInfo): Unit
    fun disconnect(id: String): Unit

    fun startHR(id: String): Flowable<PolarHrData>

    fun listRecordings()
    fun startRecording(): Completable
    fun stopRecording(): Completable
    fun deleteRecording(entry: PolarOfflineRecordingEntry)
    fun downloadRecording(recording: PolarOfflineRecordingEntry): Single<PolarOfflineRecordingData>
    fun startHeartRateStream()
    fun stopHeartRateStream()

}
