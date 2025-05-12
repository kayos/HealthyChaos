package com.kayos.polar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class DeviceManager() {

    companion object {
        private const val TAG = "DeviceManager"

        @Volatile
        private var instance: DeviceManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: DeviceManager().also { instance = it }
            }
    }

    private val _connectedDevice: MutableStateFlow<Device?> = MutableStateFlow(null)
    val connectedDevice : StateFlow<Device?> = _connectedDevice.asStateFlow()

    fun notifyDeviceConnected(device: Device){
        _connectedDevice.value = device
    }

    fun getConnectedDevice(): Device? {
        return _connectedDevice.value
    }
}

