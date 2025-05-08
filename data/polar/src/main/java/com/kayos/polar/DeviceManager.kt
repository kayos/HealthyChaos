package com.kayos.polar

import com.polar.sdk.api.PolarBleApi

class DeviceManager(api: PolarBleApi) {

    companion object {
        private const val TAG = "DeviceManager"

        @Volatile
        private var instance: DeviceManager? = null

        fun getInstance(api: PolarBleApi) =
            instance ?: synchronized(this) {
                instance ?: DeviceManager(api).also { instance = it }
            }
    }
}

