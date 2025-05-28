package com.kayos.polar

import com.polar.sdk.api.PolarBleApi
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.mock

class PolarDeviceTest {
    @Test
    fun hasRecordingsFunctionality() {
        val api = mock<PolarBleApi>{}
        val device = PolarDevice("123", "name", api)

        assertNotNull(device.getRecordingsFunctionality())
    }

    @Test
    fun hasStreamFunctionality() {
        val api = mock<PolarBleApi>{}
        val device = PolarDevice("123", "name", api)

        assertNotNull(device.getStreamFunctionality())
    }
}