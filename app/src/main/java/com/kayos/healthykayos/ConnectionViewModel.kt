package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kayos.polar.Device
import com.kayos.polar.DeviceManager
import com.kayos.polar.HeartRateProviderFactory
import com.kayos.polar.IHeartRateSensor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn


class ConnectionViewModel(
    private val _sensor: IHeartRateSensor,
     deviceManager : DeviceManager = DeviceManager.getInstance()) : ViewModel() {

    private val _shouldSearch = MutableStateFlow(false)
    private val _availableDevices: Flow<List<Device>> = _shouldSearch.flatMapLatest {
        shouldSearch ->
            if (shouldSearch) _sensor.search()
            else flowOf(emptyList<Device>())
    }

    private val _connectedDevice : StateFlow<Device?> = deviceManager.connectedDevice.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null,
    )

    val uiState: StateFlow<ConnectionUiState> = combine(_availableDevices, _connectedDevice){
        available, connectedDevice ->
        ConnectionUiState(available, connectedDevice)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ConnectionUiState(),
    )

    fun search(){
        _shouldSearch.value = true
    }

    fun connect(deviceId: String) {
        _sensor.connect(deviceId)
    }

    companion object {
        private const val TAG = "ConnectionViewModel"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                ConnectionViewModel(
                    _sensor = HeartRateProviderFactory.getPolarHeartRateSensor(application.applicationContext)
                )
            }
        }
    }
}

data class ConnectionUiState(
    val availableDevices: List<Device> = emptyList(),
    val connectedDevice: Device? = null
)
