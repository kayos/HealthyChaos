package com.kayos.healthykayos.sensor

import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Flowable

interface IHeartRateSensor {
    fun search(): Flowable<PolarDeviceInfo>
    fun connect(id: String): Unit
    fun disconnect(id: String): Unit

    fun startHR(id: String): Flowable<PolarHrData>

    fun listRecordings(id: String): Flowable<PolarOfflineRecordingEntry>
}
