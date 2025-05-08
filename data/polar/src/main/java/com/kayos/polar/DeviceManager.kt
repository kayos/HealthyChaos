package com.kayos.polar

import com.polar.sdk.api.PolarBleApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow

class DeviceManager(private val api: PolarBleApi) {

    companion object {
        private const val TAG = "DeviceManager"

        @Volatile
        private var instance: DeviceManager? = null

        fun getInstance(api: PolarBleApi) =
            instance ?: synchronized(this) {
                instance ?: DeviceManager(api).also { instance = it }
            }
    }

    fun search(): Flow<List<Device>> {
        api.setPolarFilter(true)

        val devices =  mutableListOf<Device>()
        return api.searchForDevice().map { entry ->
            devices.add(Device(entry.deviceId, entry.name))
            devices.toList()
        }.asFlow()
    }
}

