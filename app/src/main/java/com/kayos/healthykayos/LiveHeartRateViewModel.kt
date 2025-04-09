package com.kayos.healthykayos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.IHeartRateSensor

class LiveHeartRateViewModel(val sensor: IHeartRateSensor) : ViewModel()
{
    val heartRate = sensor.heartRate

    fun startStreaming() {
        sensor.startHeartRateStream()
    }

    fun stopStreaming() {
        sensor.stopHeartRateStream()
    }

    companion object {
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
