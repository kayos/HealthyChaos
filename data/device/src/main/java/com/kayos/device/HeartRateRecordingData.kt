package com.kayos.device

import java.util.Calendar

sealed class RecordingData(val startTime: Calendar){

     class HeartRateRecording(startTime: Calendar, val data: HrData) :
        RecordingData(startTime)

     class UnknownRecording(startTime: Calendar) :
        RecordingData(startTime)
}

data class HrData(val samples: List<HrSample>) {
    data class HrSample(
        val bpm: Int,
        val secondsFromStart: Long
    )
}