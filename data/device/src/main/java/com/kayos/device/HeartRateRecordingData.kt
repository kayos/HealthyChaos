package com.kayos.device

import java.time.ZoneId
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed class RecordingData(val start: Calendar){

    abstract val duration : Duration
    val startDate : java.time.LocalDate
        get() = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()  //TODO: Should this be UTC?
    val startTime : java.time.LocalTime
        get() = start.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withNano(0) //TODO: Should this be UTC?

    class HeartRateRecording(start: Calendar, val data: HrData) :
    RecordingData(start) {
     override val duration: Duration
         get() = data.samples.last().secondsFromStart.seconds
    }

    class UnknownRecording(start: Calendar) :
        RecordingData(start) {
        override val duration: Duration
            get() = 0.seconds
    }
}


data class HrData(val samples: List<HrSample>) {
    data class HrSample(
        val bpm: Int,
        val secondsFromStart: Long
    )
}