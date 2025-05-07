package com.kayos.healthykayos

import app.cash.turbine.test
import com.kayos.healthykayos.doubles.SensorStub
import com.kayos.healthykayos.testutils.MainDispatcherRule
import com.kayos.polar.Device
import com.kayos.polar.IHeartRateSensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class ConnectionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onInitialisation_noDevicesAvailable() = runTest {
        val viewModel = ConnectionViewModel(
            sensor = SensorStub(MutableStateFlow(null))
        )

        viewModel.uiState.test {
            val uiState = awaitItem()
            assertEquals(emptyList<Device>(), uiState.availableDevices)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun search_uiStateContainsFoundDevices() = runTest {
        val expected = listOf(Device("1"))
        val sensorStub = mock<IHeartRateSensor> {
            on { searchV2() } doReturn flowOf(expected)
        }

        val viewModel = ConnectionViewModel(
            sensor = sensorStub
        )

        viewModel.uiState.test {
            awaitItem()

            viewModel.search()
            val uiState = awaitItem()
            assertEquals(expected, uiState.availableDevices)

            cancelAndIgnoreRemainingEvents()
        }
    }
}