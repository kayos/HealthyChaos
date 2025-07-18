package com.kayos.device

import java.util.Calendar

sealed class RecordingData(val start: Calendar){

     class HeartRateRecording(start: Calendar, val data: HrData) :
        RecordingData(start)

     class UnknownRecording(start: Calendar) :
        RecordingData(start)
}

data class HrData(val samples: List<HrSample>) {
    data class HrSample(
        val bpm: Int,
        val secondsFromStart: Long
    )
}