package com.kayos.polar

import com.kayos.device.HrData
import com.kayos.device.RecordingData
import com.kayos.device.RecordingData.HeartRateRecording
import com.polar.sdk.api.model.PolarOfflineRecordingData

fun PolarOfflineRecordingData.convert(): RecordingData {
    return when(this) {
        is PolarOfflineRecordingData.HrOfflineRecording -> {
            this.convert()
        }
        else -> {
            RecordingData.UnknownRecording(this.startTime)
        }
    }
}

fun PolarOfflineRecordingData.HrOfflineRecording.convert(): HeartRateRecording {
    var secondFromStart: Long = 0
    val samples = this.data.samples.map{
            sample -> HrData.HrSample(sample.hr, secondFromStart++) }

    return HeartRateRecording(this.startTime,  HrData(samples))
}