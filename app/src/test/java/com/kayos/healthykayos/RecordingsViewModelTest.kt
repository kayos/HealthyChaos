package com.kayos.healthykayos

import com.kayos.healthykayos.doubles.FailingSensorStub
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.testutils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class RecordingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startRecording_setsRecordingState() {
        val viewModel = RecordingsViewModel(SensorStub(
            heartRate = MutableStateFlow(null)
        ))

        viewModel.startRecording()

        assertEquals(RecordingState.Recording(), viewModel.recordingState.value)
    }

    @Test
    fun startRecording_whenFails_setsRecordingState() {
        val viewModel = RecordingsViewModel(FailingSensorStub())

        viewModel.startRecording()

        assertEquals(RecordingState.NotRecording(), viewModel.recordingState.value)
    }

    @Test
    fun stopRecording_setsRecordingState() {
        val viewModel = RecordingsViewModel(SensorStub(
            heartRate = MutableStateFlow(null)
        ))
        viewModel.startRecording()

        viewModel.stopRecording()

        assertEquals(RecordingState.NotRecording(), viewModel.recordingState.value)
    }

    @Test
    fun stopRecording_whenFails_setsRecordingState() {
        val viewModel = RecordingsViewModel(FailingSensorStub())

        viewModel.stopRecording()

        assertEquals(RecordingState.Recording(), viewModel.recordingState.value)
    }
}