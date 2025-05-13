package com.kayos.polar

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class DeviceManagerTest {

    @Test
    fun connectedDevice_onInit_emitsNull() = runTest {
        val manager = DeviceManager()

        manager.connectedDevice.test{
            assertNull(awaitItem())
        }

    }

    @Test
    fun connectedDevice_emitsLatestConnectedDevice() = runTest {
        val device = Device("id", "Polar")
        val manager = DeviceManager()

        manager.notifyDeviceConnected(device)

        manager.connectedDevice.test{
            assertEquals(device, awaitItem())
        }

    }
}