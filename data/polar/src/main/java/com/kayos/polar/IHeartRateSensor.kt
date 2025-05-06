package com.kayos.polar

import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

// TODO: remove any reference to Polar stuff to make this a more generic interface
interface IHeartRateSensor {
    val heartRate: StateFlow<HeartRate?>
    val availableDevices: StateFlow<List<PolarDeviceInfo>>
    val connectedDevices: StateFlow<PolarDeviceInfo?>

    fun search()
    fun connect(deviceId: String)
    fun disconnect(deviceId: String)
    fun dispose()

    fun startHR(deviceId: String): Flowable<PolarHrData>

    fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>>
    fun startRecording(): Completable
    fun stopRecording(): Completable
    suspend fun deleteRecording(entry: PolarOfflineRecordingEntry)
    suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): PolarOfflineRecordingData
    suspend fun isRecording(): Boolean
    fun startHeartRateStream()
    fun stopHeartRateStream()
}