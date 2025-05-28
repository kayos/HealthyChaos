package com.kayos.polar

import android.content.Context
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl


class PolarApiFactory private constructor() {
    companion object {
        @Volatile
        private var instance: PolarBleApi? = null
        private val lock = Any()

        @JvmStatic
        fun getInstance(context: Context): PolarBleApi {
            return instance ?: synchronized(lock) {
                instance ?: getPolarApi(context).also { instance = it }
            }
        }

        @JvmStatic
        fun disposeInstance() {
            synchronized(lock) {
                instance?.shutDown()
                instance = null
            }
        }

        @JvmStatic
        private fun getPolarApi(context: Context): PolarBleApi {
            return PolarBleApiDefaultImpl.defaultImplementation(
                context,
                setOf(
                    PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
                    PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_SDK_MODE,
                    PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                    PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_OFFLINE_RECORDING,
                    PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                    PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO
                )
            )
        }
    }
}