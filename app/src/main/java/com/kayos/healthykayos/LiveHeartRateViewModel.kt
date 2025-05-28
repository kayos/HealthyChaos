package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kayos.device.HeartRate
import com.kayos.device.IStreamAPI
import com.kayos.polar.DeviceManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LiveHeartRateViewModel(deviceManager: DeviceManager = DeviceManager.getInstance()) : ViewModel()
{
    private val _shouldStream = MutableStateFlow(false)
    private val _streamingDevice : StateFlow<IStreamAPI?> =
        deviceManager.connectedDevice.map {
                device -> device?.getStreamFunctionality()}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = null,
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    val heartRate: StateFlow<HeartRate?> = combine(
        _shouldStream,
        _streamingDevice.filterNotNull()
    ){ streaming, device -> Pair(streaming,device) }
    .flatMapLatest{ (streaming, device) ->
        if (streaming) device.startStream()
        else { flowOf(null) }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null,
    )

    fun startStreaming() {
        _shouldStream.value = true
    }

    fun stopStreaming() {
        _shouldStream.value = false
    }

    companion object {
        private const val TAG = "LiveHearRateViewModel"
    }
}
