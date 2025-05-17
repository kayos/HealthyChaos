package com.kayos.polar

import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHealthThermometerData
import com.polar.sdk.api.model.PolarHrData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow

class PolarBluetoothAdapter(
    private val api: PolarBleApi,
    private val _deviceManager : DeviceManager = DeviceManager.getInstance())
{
    companion object {
        private const val TAG = "PolarBluetoothAdapter"
    }

    init{
        val callback = object : PolarBleApiCallback() {
            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                val device = PolarDevice(polarDeviceInfo.deviceId, polarDeviceInfo.name,api)
                _deviceManager.notifyDeviceConnected(device)
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
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