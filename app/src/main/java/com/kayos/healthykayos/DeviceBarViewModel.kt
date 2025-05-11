package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import com.kayos.polar.Device
import com.kayos.polar.DeviceManager
import kotlinx.coroutines.flow.StateFlow

class DeviceBarViewModel : ViewModel() {

    val connectedDevice: StateFlow<Device?> get() = DeviceManager.getInstance().connectedDevice
}
