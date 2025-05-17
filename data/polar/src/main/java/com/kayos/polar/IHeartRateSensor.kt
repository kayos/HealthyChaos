package com.kayos.polar

import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/*** TODO Make interface more generic, tricky to do with only one type example
 * waiting to see what use-cases need and other devices offer
 * */
interface IHeartRateSensor {
    val heartRate: StateFlow<HeartRate?>

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