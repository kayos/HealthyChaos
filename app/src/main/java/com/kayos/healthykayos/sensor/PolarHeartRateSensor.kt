package com.kayos.healthykayos.sensor

import android.content.Context
import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHealthThermometerData
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import com.polar.sdk.api.model.PolarSensorSetting
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.rx3.await
import java.time.Instant
import java.util.UUID

class PolarHeartRateSensor private constructor(context: Context): IHeartRateSensor {

    private  var recordingDisposable : Disposable? = null
    private  var deleteDisposable : Disposable? = null
    private val _recordings = MutableStateFlow<List<PolarOfflineRecordingEntry>>(emptyList())
    override val recordings: StateFlow<List<PolarOfflineRecordingEntry>> get() = _recordings

    private  var searchDisposable : Disposable? = null
    private val _availableDevices = MutableStateFlow<List<PolarDeviceInfo>>(emptyList())
    val availableDevices: StateFlow<List<PolarDeviceInfo>> get() = _availableDevices

    private val _connectedDevices = MutableStateFlow<PolarDeviceInfo?>(null)
    val connectedDevices: StateFlow<PolarDeviceInfo?> get() = _connectedDevices

    private var heartRateDisposable : Disposable? = null
    private val _heartRate = MutableStateFlow<HeartRate?>(null)
    override val heartRate: StateFlow<HeartRate?> get() = _heartRate

    // TODO: set this when specific device is connected, rethink sharing between fragments
    var selectedDeviceId : String? = null

    val hrSampleRateSec = 1

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

    init {
        api.setApiCallback(object : PolarBleApiCallback() {
            override fun blePowerStateChanged(powered: Boolean) {
                Log.d(TAG, "BLE power: $powered")
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                selectedDeviceId = polarDeviceInfo.deviceId
                _connectedDevices.value = polarDeviceInfo
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
                _connectedDevices.value = null
            }

            override fun disInformationReceived(
                identifier: String,
                disInfo: DisInfo
            ) {
                TODO("Not yet implemented")
            }

            override fun disInformationReceived(identifier: String, uuid: UUID, value: String) {
                Log.d(TAG, "DIS INFO uuid: $uuid value: $value")
            }

            override fun batteryLevelReceived(identifier: String, level: Int) {
                Log.d(TAG, "BATTERY LEVEL: $level")
            }

            override fun hrNotificationReceived(identifier: String, data: PolarHrData.PolarHrSample) {
                // deprecated
            }

            override fun htsNotificationReceived(
                identifier: String,
                data: PolarHealthThermometerData
            ) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun search() {
        _availableDevices.value = emptyList<PolarDeviceInfo>()

        api.setPolarFilter(true);

        searchDisposable?.dispose()
        searchDisposable = api.searchForDevice()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {  polarDeviceInfo: PolarDeviceInfo ->
                    _availableDevices.value = _availableDevices.value + polarDeviceInfo
                    Log.d(TAG, "Found Device: ${polarDeviceInfo.name}")
                },
                onError = { error: Throwable ->
                    Log.e(TAG, "Failed to search: $error")
                },
                onComplete = {
                    searchDisposable?.dispose()
                    Log.d(TAG, "Done searching for devices")
                }
            )
    }

    override fun connect(device: PolarDeviceInfo) {
        api.connectToDevice(device.deviceId)
    }

    override fun disconnect(id: String) {
        api.disconnectFromDevice(id)
    }

    override fun startHR(id: String): Flowable<PolarHrData> {
        return api.startHrStreaming(id)
    }

    override fun startRecording(): Completable {
        return api.startOfflineRecording(selectedDeviceId!!, PolarBleApi.PolarDeviceDataType.HR)
    }

    override fun stopRecording(): Completable {
        return api.stopOfflineRecording(selectedDeviceId!!, PolarBleApi.PolarDeviceDataType.HR)
    }

    override fun deleteRecording(entry: PolarOfflineRecordingEntry) {
        deleteDisposable?.dispose()
        deleteDisposable = api.removeOfflineRecord(selectedDeviceId!!, entry)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = { error: Throwable ->
                    Log.e(TAG, "Failed to delete recording: $error")
                },
                onComplete = {
                    _recordings.value = _recordings.value - entry
                    deleteDisposable?.dispose()
                    Log.d(TAG, "Done deleting recording")
                }
            )
    }

    override fun downloadRecording(recording: PolarOfflineRecordingEntry): Single<PolarOfflineRecordingData> {
        return api.getOfflineRecord(selectedDeviceId!!, recording)
    }

    override suspend fun isRecording(): Boolean {
        return api.getOfflineRecordingStatus(selectedDeviceId!!).map {
            runningRecordings -> runningRecordings.contains(PolarBleApi.PolarDeviceDataType.HR)
        }.await()
    }

    override fun startHeartRateStream() {
       heartRateDisposable?.dispose()
       heartRateDisposable = api.startHrStreaming(selectedDeviceId!!)
           .flatMap{data -> Flowable.fromIterable(data.samples)}
           .map{sample -> HeartRate(Instant.now(), sample.hr)}
           .subscribeBy(
               onNext = {  heartRate: HeartRate ->
                   _heartRate.value = heartRate
               },
               onError = { error: Throwable ->
                   Log.e(TAG, "Failed streaming heart rate: $error")
               },
               onComplete = {
                   Log.d(TAG, "Done streaming heart rate")
               }
           )
    }

    override fun stopHeartRateStream() {
        heartRateDisposable?.dispose()
        _heartRate.value = null
    }

    override fun listRecordings() {
        recordingDisposable?.dispose()
        _recordings.value = emptyList()
        recordingDisposable = api.listOfflineRecordings(selectedDeviceId!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { recording: PolarOfflineRecordingEntry ->
                    _recordings.value = _recordings.value + recording
                    Log.d(TAG, "Found: ${recording.date}")
                },
                onError = { error: Throwable ->
                    Log.e(TAG, "Failed to list recordings: $error")
                },
                onComplete = {
                    recordingDisposable?.dispose()
                    Log.d(TAG, "Done searching for recordings")
                }
            )
    }

}