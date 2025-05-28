package com.kayos.healthykayos

import app.cash.turbine.test
import com.kayos.device.HeartRate
import com.kayos.device.IStreamAPI
import com.kayos.healthykayos.testutils.MainDispatcherRule
import com.kayos.polar.Device
import com.kayos.polar.DeviceManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class LiveHeartRateViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun startStreaming_emitsHeartRate() = runTest {
        val expected = HeartRate(Instant.now(), 123)
        val streamApi = mock<IStreamAPI> {
            on { startStream() } doReturn flowOf(expected)
        }
        val device = mock<Device>{
            on { getStreamFunctionality() } doReturn streamApi
        }
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(device)
        val viewModel = LiveHeartRateViewModel(deviceManager)

        viewModel.heartRate.test{
            awaitItem()
            viewModel.startStreaming()

            assertEquals(expected, awaitItem())
        }
    }

    @Test
    fun stopStreaming_stopsHeartRateEmission() = runTest {
        val expected = HeartRate(Instant.now(), 123)
        val streamApi = mock<IStreamAPI> {
            on { startStream() } doReturn flowOf(expected)
        }
        val device = mock<Device>{
            on { getStreamFunctionality() } doReturn streamApi
        }
        val deviceManager = DeviceManager()
        deviceManager.notifyDeviceConnected(device)
        val viewModel = LiveHeartRateViewModel(deviceManager)

        viewModel.heartRate.test{
            awaitItem()
            viewModel.startStreaming()
            assertEquals(expected, awaitItem())
            viewModel.stopStreaming()
            assertNull(awaitItem())
        }
    }
}