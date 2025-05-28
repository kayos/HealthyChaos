package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kayos.polar.DeviceManager
import com.kayos.polar.HeartRate
import com.kayos.polar.HeartRateProviderFactory
import com.kayos.polar.IHeartRateSensor
import com.kayos.polar.IStreamAPI
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

class LiveHeartRateViewModel(val sensor: IHeartRateSensor,
                             deviceManager: DeviceManager = DeviceManager.getInstance()) : ViewModel()
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

   // val heartRate = sensor.heartRate

    fun startStreaming() {
        sensor.startHeartRateStream()
        _shouldStream.value = true
    }

    fun stopStreaming() {
        sensor.stopHeartRateStream()
        _shouldStream.value = false
    }

    companion object {
        private const val TAG = "LiveHearRateViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                LiveHeartRateViewModel(
                    sensor = HeartRateProviderFactory.getPolarHeartRateSensor(application.applicationContext)
               )
            }
        }
    }
}
