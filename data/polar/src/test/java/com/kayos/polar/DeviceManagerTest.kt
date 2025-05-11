package com.kayos.polar

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*

class DeviceManagerTest {

    @Test
    fun notifyDeviceConnected_() = runTest {
        val device = Device("id", "Polar")
        val manager = DeviceManager()

        manager.connectedDevice.test{
            awaitItem() //first null device
            manager.notifyDeviceConnected(device)

            assertEquals(device, awaitItem())
        }
    }
}