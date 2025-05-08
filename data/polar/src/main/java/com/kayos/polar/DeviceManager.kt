package com.kayos.polar

import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHealthThermometerData
import com.polar.sdk.api.model.PolarHrData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    val connectedDevice : Flow<Device?> = callbackFlow {
        val callback = object : PolarBleApiCallback() {
            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                trySend(Device(polarDeviceInfo.deviceId, polarDeviceInfo.name))
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
                trySend(null)
            }

            override fun batteryLevelReceived(identifier: String, level: Int) {
                Log.d(TAG, "BATTERY LEVEL: $level")
            }

            override fun blePowerStateChanged(powered: Boolean) {
                //don't care
            }

            override fun disInformationReceived(
                identifier: String,
                disInfo: DisInfo
            ) {
                // don't care
            }

            override fun hrNotificationReceived(
                identifier: String,
                data: PolarHrData.PolarHrSample
            ) {
                // deprecated
            }

            override fun htsNotificationReceived(
                identifier: String,
                data: PolarHealthThermometerData
            ) {
                // don't care
            }

        }
        api.setApiCallback(callback)

        awaitClose {
           //TODO figure out to unregister from api
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

    fun connect(deviceId: String) {
        api.connectToDevice(deviceId)
    }

    fun disconnect(deviceId: String) {
        api.disconnectFromDevice(deviceId)
    }
}

