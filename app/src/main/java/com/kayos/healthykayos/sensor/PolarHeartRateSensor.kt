package com.kayos.healthykayos.sensor

import android.content.Context
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.model.PolarDeviceInfo
import io.reactivex.rxjava3.core.Flowable

class PolarHeartRateSensor private constructor(context: Context): IHeartRateSensor {
    //TODO make private once refactor is done
    val api: PolarBleApi = PolarBleApiDefaultImpl.defaultImplementation(
            context,
            setOf(
                PolarBleApi.PolarBleSdkFeature.FEATURE_HR,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_SDK_MODE,
                PolarBleApi.PolarBleSdkFeature.FEATURE_BATTERY_INFO,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_OFFLINE_RECORDING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_ONLINE_STREAMING,
                PolarBleApi.PolarBleSdkFeature.FEATURE_DEVICE_INFO,
                PolarBleApi.PolarBleSdkFeature.FEATURE_POLAR_LED_ANIMATION
            )
        )

    companion object {
        private const val TAG = "PolarHeartRateSensor"
        @Volatile
        private var instance: PolarHeartRateSensor? = null

        fun getInstance(context : Context) =
            instance ?: synchronized(this) {
                instance ?: PolarHeartRateSensor(context).also { instance = it }
            }
    }

    override fun search(): Flowable<PolarDeviceInfo> {
        api.setPolarFilter(true);
        return api.searchForDevice()
    }

    override fun connect(id: String) {
        api.connectToDevice(id)
    }

    override fun disconnect(id: String) {
        api.disconnectFromDevice(id)
    }

}