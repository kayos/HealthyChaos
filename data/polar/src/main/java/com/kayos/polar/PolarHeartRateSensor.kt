package com.kayos.polar

import android.content.Context
import android.util.Log
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHealthThermometerData
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx3.await
import java.time.Instant

internal class PolarHeartRateSensor private constructor(
    context: Context,
    private val _deviceManager : DeviceManager = DeviceManager.getInstance()) : IHeartRateSensor {

    private val _availableDevices = MutableStateFlow<List<PolarDeviceInfo>>(emptyList())
    override val availableDevices: StateFlow<List<PolarDeviceInfo>> get() = _availableDevices

    private var heartRateDisposable: Disposable? = null
    private val _heartRate = MutableStateFlow<HeartRate?>(null)
    override val heartRate: StateFlow<HeartRate?> get() = _heartRate

    //TODO make private once refactor is done
    val api: PolarBleApi = PolarApiFactory.getPolarApi(context)

    companion object {
        private const val TAG = "PolarHeartRateSensor"

        @Volatile
        private var instance: PolarHeartRateSensor? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: PolarHeartRateSensor(context).also { instance = it }
            }
    }

    private val _connectedDevice = MutableStateFlow<Device?>(null)
    override val connectedDevice : Flow<Device?> = _connectedDevice

    init{
        val callback = object : PolarBleApiCallback() {
            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                _deviceManager.notifyDeviceConnected(
                    Device(polarDeviceInfo.deviceId, polarDeviceInfo.name))
                _connectedDevice.value =  Device(polarDeviceInfo.deviceId, polarDeviceInfo.name)
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

    override fun search(): Flow<List<Device>> {
        api.setPolarFilter(true)

        val devices =  mutableListOf<Device>()
        return api.searchForDevice().map { entry ->
            devices.add(Device(entry.deviceId, entry.name))
            devices.toList()
        }.asFlow()
    }

    override fun connect(deviceId: String) {
        api.connectToDevice(deviceId)
    }

    override fun disconnect(deviceId: String) {
        api.disconnectFromDevice(deviceId)
    }

    override fun dispose(){
        api.shutDown()
    }

    override fun startHR(deviceId: String): Flowable<PolarHrData> {
        return api.startHrStreaming(deviceId)
    }

    override fun startRecording(): Completable {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        return api.startOfflineRecording(deviceId, PolarBleApi.PolarDeviceDataType.HR)
    }

    override fun stopRecording(): Completable {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        return api.stopOfflineRecording(deviceId, PolarBleApi.PolarDeviceDataType.HR)
    }

    override suspend fun deleteRecording(entry: PolarOfflineRecordingEntry) {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        api.removeOfflineRecord(deviceId, entry).await()
        listRecordings()
    }

    override suspend fun downloadRecording(recording: PolarOfflineRecordingEntry): PolarOfflineRecordingData {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        return api.getOfflineRecord(deviceId, recording).await()
    }

    override suspend fun isRecording(): Boolean {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        return api.getOfflineRecordingStatus(deviceId).map { runningRecordings ->
            runningRecordings.contains(PolarBleApi.PolarDeviceDataType.HR)
        }.await()
    }

    override fun startHeartRateStream() {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        heartRateDisposable?.dispose()
        heartRateDisposable = api.startHrStreaming(deviceId)
            .flatMap { data -> Flowable.fromIterable(data.samples) }
            .map { sample -> HeartRate(Instant.now(), sample.hr) }
            .subscribeBy(
                onNext = { heartRate: HeartRate ->
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

    override fun listRecordings(): Flow<List<PolarOfflineRecordingEntry>> {
        val deviceId = _deviceManager.getConnectedDevice()!!.id
        val recordings = mutableListOf<PolarOfflineRecordingEntry>()
        return api.listOfflineRecordings(deviceId).map { entry ->
            recordings.add(entry)
            recordings.toList()
        }.asFlow()
    }

}