package com.kayos.healthykayos

import app.cash.turbine.test
import com.kayos.healthykayos.testutils.MainDispatcherRule
import com.kayos.polar.Device
import com.kayos.polar.PolarBluetoothAdapter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConnectionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onInitialisation_noDevicesAvailable() = runTest {
        val viewModel = ConnectionViewModel(
            _bluetoothAdapter = mock<PolarBluetoothAdapter>{}
        )

        viewModel.uiState.test {
            val uiState = awaitItem()
            assertEquals(emptyList<Device>(), uiState.availableDevices)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun search_uiStateContainsFoundDevices() = runTest {
        val expected = listOf(Device("1", "name"))
        val bluetoothStub = mock<PolarBluetoothAdapter> {
            on { search() } doReturn flowOf(expected)
        }

        val viewModel = ConnectionViewModel(
            _bluetoothAdapter = bluetoothStub
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