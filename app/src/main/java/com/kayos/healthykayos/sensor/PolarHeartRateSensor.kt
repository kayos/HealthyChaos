package com.kayos.healthykayos.sensor

import android.content.Context
import android.util.Log
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PolarHeartRateSensor private constructor(context: Context): IHeartRateSensor {

    private  var recordingDisposable : Disposable? = null
    private val _recordings = MutableStateFlow<List<PolarOfflineRecordingEntry>>(emptyList())
    val recordings: StateFlow<List<PolarOfflineRecordingEntry>> get() = _recordings


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

    override fun startHR(id: String): Flowable<PolarHrData> {
        return api.startHrStreaming(id)
    }

    override fun listRecordings(id: String) {
        recordingDisposable?.dispose()
        recordingDisposable = api.listOfflineRecordings(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { recording: PolarOfflineRecordingEntry ->
                    _recordings.value = _recordings.value + recording
                },
                onError = { error: Throwable ->
                    Log.e(TAG, "Failed to list recordings: $error")
                },
                onComplete = { recordingDisposable?.dispose() }
            )
    }

}