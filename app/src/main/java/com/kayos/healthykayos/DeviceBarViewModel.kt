package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DeviceBarViewModel : ViewModel() {
    private val _connectedDevice: MutableStateFlow<Device?> = MutableStateFlow(null)
    val connectedDevice: StateFlow<Device?> get() = _connectedDevice
}
