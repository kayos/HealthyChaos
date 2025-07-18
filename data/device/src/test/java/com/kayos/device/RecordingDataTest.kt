package com.kayos.device

import java.util.Calendar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class RecordingDataTest {

    @Test
    fun recordingData_startTime() {
        val cal = Calendar.getInstance()
        cal.set(2024, Calendar.JUNE, 1, 12, 30, 0)
        val data = HrData(emptyList())
        val recording = RecordingData.HeartRateRecording(cal, data)
        val expectedTime = java.time.LocalTime.of(12, 30, 0)

        assertEquals(expectedTime, recording.startTime)
    }

    @Test
    fun recordingData_startDate() {
        val cal = Calendar.getInstance()
        cal.set(2024, Calendar.JUNE, 1, 12, 30, 0)
        val data = HrData(emptyList())
        val recording = RecordingData.HeartRateRecording(cal, data)
        val expectedDate = java.time.LocalDate.of(2024, 6, 1)

        assertEquals(expectedDate, recording.startDate)
    }

    @Test
    fun heartRateRecording_duration_returnsTotalExerciseDuration() {
        val cal = Calendar.getInstance()
        val samples = listOf(
            HrData.HrSample(70, 0),
            HrData.HrSample(75, 10),
            HrData.HrSample(80, 20)
        )
        val data = HrData(samples)
        val recording = RecordingData.HeartRateRecording(cal, data)

        assertEquals(20.seconds, recording.duration)
    }
}