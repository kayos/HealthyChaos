package com.kayos.healthykayos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.snackbar.Snackbar
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.PolarHeartRateSensor
import com.polar.androidcommunications.api.ble.model.DisInfo
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarGyroData
import com.polar.sdk.api.model.PolarHealthThermometerData
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarMagnetometerData
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import com.polar.sdk.api.model.PolarPpgData
import com.polar.sdk.api.model.PolarPpiData
import com.polar.sdk.api.model.PolarRecordingSecret
import com.polar.sdk.api.model.PolarSensorSetting
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val API_LOGGER_TAG = "API LOGGER"
        private const val PERMISSION_REQUEST_CODE = 1
    }

    // ATTENTION! Replace with the device ID from your device.
    private var deviceId = ""

    private val sensor: PolarHeartRateSensor by lazy {
        HeartRateProviderFactory.getPolarHeartRateSensor(applicationContext)
    }

    private var scanDisposable: Disposable? = null
    private var hrDisposable: Disposable? = null
    private var accDisposable: Disposable? = null
    private var gyrDisposable: Disposable? = null
    private var magDisposable: Disposable? = null
    private var ppgDisposable: Disposable? = null
    private var ppiDisposable: Disposable? = null

    private var deviceConnected = false
    private var bluetoothEnabled = false

    private lateinit var connectButton: Button
    private lateinit var scanButton: Button
    private lateinit var hrButton: Button
    private lateinit var accButton: Button
    private lateinit var gyrButton: Button
    private lateinit var magButton: Button
    private lateinit var ppgButton: Button
    private lateinit var ppiButton: Button

    //Verity Sense offline recording use
    private lateinit var startRecordingButton: Button
    private lateinit var stopRecordingButton: Button
    private lateinit var downloadRecordingButton: Button
    private lateinit var deleteRecordingButton: Button
    private val entryCache: MutableMap<String, MutableList<PolarOfflineRecordingEntry>> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "version: " + PolarBleApiDefaultImpl.versionInfo())
        connectButton = findViewById(R.id.connect_button)
        scanButton = findViewById(R.id.scan_button)
        hrButton = findViewById(R.id.hr_button)
        accButton = findViewById(R.id.acc_button)
        gyrButton = findViewById(R.id.gyr_button)
        magButton = findViewById(R.id.mag_button)
        ppgButton = findViewById(R.id.ohr_ppg_button)
        ppiButton = findViewById(R.id.ohr_ppi_button)

        //Verity Sense recording buttons
        startRecordingButton = findViewById(R.id.start_recording)
        stopRecordingButton = findViewById(R.id.stop_recording)
        downloadRecordingButton = findViewById(R.id.download_recording)
        deleteRecordingButton = findViewById(R.id.delete_recording)


        //COMPOSE ---------------
        val composeView = findViewById<ComposeView>(R.id.recordings_view)
        composeView.apply {
            setContent {
                MaterialTheme {
                    Recordings()
                }
            }
        }
        //---------------------------

        sensor.api.setPolarFilter(false)

        // If there is need to log what is happening inside the SDK, it can be enabled like this:
        val enableSdkLogs = false
        if(enableSdkLogs) {
            sensor.api.setApiLogger { s: String -> Log.d(API_LOGGER_TAG, s) }
        }

        sensor.api.setApiCallback(object : PolarBleApiCallback() {
            override fun blePowerStateChanged(powered: Boolean) {
                Log.d(TAG, "BLE power: $powered")
                bluetoothEnabled = powered
                if (powered) {
                    enableAllButtons()
                    showToast("Phone Bluetooth on")
                } else {
                    disableAllButtons()
                    showToast("Phone Bluetooth off")
                }
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTED: ${polarDeviceInfo.deviceId}")
                deviceId = polarDeviceInfo.deviceId
                deviceConnected = true
                val buttonText = getString(R.string.disconnect_from_device, deviceId)
                toggleButtonDown(connectButton, buttonText)
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "CONNECTING: ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "DISCONNECTED: ${polarDeviceInfo.deviceId}")
                deviceConnected = false
                val buttonText = getString(R.string.connect_to_device, deviceId)
                toggleButtonUp(connectButton, buttonText)
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

        connectButton.text = getString(R.string.connect_to_device, deviceId)
        connectButton.setOnClickListener {
            try {
                if (deviceConnected) {
                    sensor.disconnect(deviceId)
                } else {
                    sensor.connect(deviceId)
                }
            } catch (polarInvalidArgument: PolarInvalidArgument) {
                val attempt = if (deviceConnected) {
                    "disconnect"
                } else {
                    "connect"
                }
                Log.e(TAG, "Failed to $attempt. Reason $polarInvalidArgument ")
            }
        }

        scanButton.setOnClickListener {
            val isDisposed = scanDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(scanButton, R.string.scanning_devices)
                scanDisposable = sensor.search()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { polarDeviceInfo: PolarDeviceInfo ->
                            Log.d(TAG, "polar device found id: " + polarDeviceInfo.deviceId + " address: " + polarDeviceInfo.address + " rssi: " + polarDeviceInfo.rssi + " name: " + polarDeviceInfo.name + " isConnectable: " + polarDeviceInfo.isConnectable)
                        },
                        { error: Throwable ->
                            toggleButtonUp(scanButton, "Scan devices")
                            Log.e(TAG, "Device scan failed. Reason $error")
                        },
                        {
                            toggleButtonUp(scanButton, "Scan devices")
                            Log.d(TAG, "complete")
                        }
                    )
            } else {
                toggleButtonUp(scanButton, "Scan devices")
                scanDisposable?.dispose()
            }
        }

        hrButton.setOnClickListener {
            val isDisposed = hrDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(hrButton, R.string.stop_hr_stream)
                hrDisposable = sensor.startHR(deviceId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { hrData: PolarHrData ->
                            for (sample in hrData.samples) {
                                Log.d(TAG, "HR     bpm: ${sample.hr} rrs: ${sample.rrsMs} rrAvailable: ${sample.rrAvailable} contactStatus: ${sample.contactStatus} contactStatusSupported: ${sample.contactStatusSupported}")
                            }
                        },
                        { error: Throwable ->
                            toggleButtonUp(hrButton, R.string.start_hr_stream)
                            Log.e(TAG, "HR stream failed. Reason $error")
                        },
                        { Log.d(TAG, "HR stream complete") }
                    )
            } else {
                toggleButtonUp(hrButton, R.string.start_hr_stream)
                // NOTE dispose will stop streaming if it is "running"
                hrDisposable?.dispose()
            }
        }

        accButton.setOnClickListener {
            val isDisposed = accDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(accButton, R.string.stop_acc_stream)
                accDisposable = requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.ACC)
                    .flatMap { settings: PolarSensorSetting ->
                        sensor.api.startAccStreaming(deviceId, settings)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { polarAccelerometerData: PolarAccelerometerData ->
                            for (data in polarAccelerometerData.samples) {
                                Log.d(TAG, "ACC    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                            }
                        },
                        { error: Throwable ->
                            toggleButtonUp(accButton, R.string.start_acc_stream)
                            Log.e(TAG, "ACC stream failed. Reason $error")
                        },
                        {
                            showToast("ACC stream complete")
                            Log.d(TAG, "ACC stream complete")
                        }
                    )
            } else {
                toggleButtonUp(accButton, R.string.start_acc_stream)
                // NOTE dispose will stop streaming if it is "running"
                accDisposable?.dispose()
            }
        }

        gyrButton.setOnClickListener {
            val isDisposed = gyrDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(gyrButton, R.string.stop_gyro_stream)
                gyrDisposable =
                    requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.GYRO)
                        .flatMap { settings: PolarSensorSetting ->
                            sensor.api.startGyroStreaming(deviceId, settings)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { polarGyroData: PolarGyroData ->
                                for (data in polarGyroData.samples) {
                                    Log.d(TAG, "GYR    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                                }
                            },
                            { error: Throwable ->
                                toggleButtonUp(gyrButton, R.string.start_gyro_stream)
                                Log.e(TAG, "GYR stream failed. Reason $error")
                            },
                            { Log.d(TAG, "GYR stream complete") }
                        )
            } else {
                toggleButtonUp(gyrButton, R.string.start_gyro_stream)
                // NOTE dispose will stop streaming if it is "running"
                gyrDisposable?.dispose()
            }
        }

        magButton.setOnClickListener {
            val isDisposed = magDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(magButton, R.string.stop_mag_stream)
                magDisposable =
                    requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.MAGNETOMETER)
                        .flatMap { settings: PolarSensorSetting ->
                            sensor.api.startMagnetometerStreaming(deviceId, settings)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { polarMagData: PolarMagnetometerData ->
                                for (data in polarMagData.samples) {
                                    Log.d(TAG, "MAG    x: ${data.x} y: ${data.y} z: ${data.z} timeStamp: ${data.timeStamp}")
                                }
                            },
                            { error: Throwable ->
                                toggleButtonUp(magButton, R.string.start_mag_stream)
                                Log.e(TAG, "MAGNETOMETER stream failed. Reason $error")
                            },
                            { Log.d(TAG, "MAGNETOMETER stream complete") }
                        )
            } else {
                toggleButtonUp(magButton, R.string.start_mag_stream)
                // NOTE dispose will stop streaming if it is "running"
                magDisposable!!.dispose()
            }
        }

        ppgButton.setOnClickListener {
            val isDisposed = ppgDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(ppgButton, R.string.stop_ppg_stream)
                ppgDisposable =
                    requestStreamSettings(deviceId, PolarBleApi.PolarDeviceDataType.PPG)
                        .flatMap { settings: PolarSensorSetting ->
                            sensor.api.startPpgStreaming(deviceId, settings)
                        }
                        .subscribe(
                            { polarPpgData: PolarPpgData ->
                                if (polarPpgData.type == PolarPpgData.PpgDataType.PPG3_AMBIENT1) {
                                    for (data in polarPpgData.samples) {
                                        Log.d(TAG, "PPG    ppg0: ${data.channelSamples[0]} ppg1: ${data.channelSamples[1]} ppg2: ${data.channelSamples[2]} ambient: ${data.channelSamples[3]} timeStamp: ${data.timeStamp}")
                                    }
                                }
                            },
                            { error: Throwable ->
                                toggleButtonUp(ppgButton, R.string.start_ppg_stream)
                                Log.e(TAG, "PPG stream failed. Reason $error")
                            },
                            { Log.d(TAG, "PPG stream complete") }
                        )
            } else {
                toggleButtonUp(ppgButton, R.string.start_ppg_stream)
                // NOTE dispose will stop streaming if it is "running"
                ppgDisposable?.dispose()
            }
        }

        ppiButton.setOnClickListener {
            val isDisposed = ppiDisposable?.isDisposed ?: true
            if (isDisposed) {
                toggleButtonDown(ppiButton, R.string.stop_ppi_stream)
                ppiDisposable = sensor.api.startPpiStreaming(deviceId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { ppiData: PolarPpiData ->
                            for (sample in ppiData.samples) {
                                Log.d(TAG, "PPI    ppi: ${sample.ppi} blocker: ${sample.blockerBit} errorEstimate: ${sample.errorEstimate}")
                            }
                        },
                        { error: Throwable ->
                            toggleButtonUp(ppiButton, R.string.start_ppi_stream)
                            Log.e(TAG, "PPI stream failed. Reason $error")
                        },
                        { Log.d(TAG, "PPI stream complete") }
                    )
            } else {
                toggleButtonUp(ppiButton, R.string.start_ppi_stream)
                // NOTE dispose will stop streaming if it is "running"
                ppiDisposable?.dispose()
            }
        }

        startRecordingButton.setOnClickListener {
            //Example of starting ACC offline recording
            Log.d(TAG, "Starts ACC recording")
            val settings: MutableMap<PolarSensorSetting.SettingType, Int> = mutableMapOf()
            settings[PolarSensorSetting.SettingType.SAMPLE_RATE] = 52
            settings[PolarSensorSetting.SettingType.RESOLUTION] = 16
            settings[PolarSensorSetting.SettingType.RANGE] = 8
            settings[PolarSensorSetting.SettingType.CHANNELS] = 3
            //Using a secret key managed by your own.
            //  You can use a different key to each start recording calls.
            //  When using key at start recording, it is also needed for the recording download, otherwise could not be decrypted
            val yourSecret = PolarRecordingSecret(
                byteArrayOf(
                    0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                    0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
                )
            )
            sensor.api.startOfflineRecording(deviceId, PolarBleApi.PolarDeviceDataType.ACC, PolarSensorSetting(settings.toMap()), yourSecret)
                //Without a secret key
                //api.startOfflineRecording(deviceId, PolarBleApi.PolarDeviceDataType.ACC, PolarSensorSetting(settings.toMap()))
                .subscribe(
                    { Log.d(TAG, "start offline recording completed") },
                    { throwable: Throwable -> Log.e(TAG, "" + throwable.toString()) }
                )
        }

        stopRecordingButton.setOnClickListener {
            //Example of stopping ACC offline recording
            Log.d(TAG, "Stops ACC recording")
            sensor.api.stopOfflineRecording(deviceId, PolarBleApi.PolarDeviceDataType.ACC)
                .subscribe(
                    { Log.d(TAG, "stop offline recording completed") },
                    { throwable: Throwable -> Log.e(TAG, "" + throwable.toString()) }
                )
        }

        downloadRecordingButton.setOnClickListener {
            //Example of one offline recording download
            //NOTE: For this example you need to click on listRecordingsButton to have files entry (entryCache) up to date
            Log.d(TAG, "Searching to recording to download... ")
            //Get first entry for testing download
            val offlineRecEntry = entryCache[deviceId]?.firstOrNull()
            offlineRecEntry?.let { offlineEntry ->
                try {
                    //Using a secret key managed by your own.
                    //  You can use a different key to each start recording calls.
                    //  When using key at start recording, it is also needed for the recording download, otherwise could not be decrypted
                    val yourSecret = PolarRecordingSecret(
                        byteArrayOf(
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,
                            0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07
                        )
                    )
                    sensor.api.getOfflineRecord(deviceId, offlineEntry, yourSecret)
                        //Not using a secret key
                        //api.getOfflineRecord(deviceId, offlineEntry)
                        .subscribe(
                            {
                                Log.d(TAG, "Recording ${offlineEntry.path} downloaded. Size: ${offlineEntry.size}")
                                when (it) {
                                    is PolarOfflineRecordingData.AccOfflineRecording -> {
                                        Log.d(TAG, "ACC Recording started at ${it.startTime}")
                                        for (sample in it.data.samples) {
                                            Log.d(TAG, "ACC data: time: ${sample.timeStamp} X: ${sample.x} Y: ${sample.y} Z: ${sample.z}")
                                        }
                                    }
//                      is PolarOfflineRecordingData.GyroOfflineRecording -> { }
//                      is PolarOfflineRecordingData.MagOfflineRecording -> { }
//                      ...
                                    else -> {
                                        Log.d(TAG, "Recording type is not yet implemented")
                                    }
                                }
                            },
                            { throwable: Throwable -> Log.e(TAG, "" + throwable.toString()) }
                        )
                } catch (e: Exception) {
                    Log.e(TAG, "Get offline recording fetch failed on entry ...", e)
                }
            }
        }

        deleteRecordingButton.setOnClickListener {
            //Example of one offline recording deletion
            //NOTE: For this example you need to click on listRecordingsButton to have files entry (entryCache) up to date
            Log.d(TAG, "Searching to recording to delete... ")
            //Get first entry for testing deletion
            val offlineRecEntry = entryCache[deviceId]?.firstOrNull()
            offlineRecEntry?.let { offlineEntry ->
                try {
                    sensor.api.removeOfflineRecord(deviceId, offlineEntry)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                Log.d(TAG, "Recording file deleted")
                            },
                            { error ->
                                val errorString = "Recording file deletion failed: $error"
                                showToast(errorString)
                                Log.e(TAG, errorString)
                            }
                        )

                } catch (e: Exception) {
                    Log.e(TAG, "Delete offline recording failed on entry ...", e)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQUEST_CODE)
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    @Composable
    fun Recordings() {
        val recordings = sensor.recordings.collectAsState().value
        Column {
            Button(onClick = {
                sensor.listRecordings(deviceId)
            }) {
                Text("Refresh")
            }
            Column {
                recordings.forEach { recording ->
                    Text(recording.date.toString(), color = Color.White)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (index in 0..grantResults.lastIndex) {
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    disableAllButtons()
                    Log.w(TAG, "No sufficient permissions")
                    showToast("No sufficient permissions")
                    return
                }
            }
            Log.d(TAG, "Needed permissions are granted")
            enableAllButtons()
        }
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onResume() {
        super.onResume()
        sensor.api.foregroundEntered()
    }

    public override fun onDestroy() {
        super.onDestroy()
        sensor.api.shutDown()
    }

    private fun toggleButtonDown(button: Button, text: String? = null) {
        toggleButton(button, true, text)
    }

    private fun toggleButtonDown(button: Button, @StringRes resourceId: Int) {
        toggleButton(button, true, getString(resourceId))
    }

    private fun toggleButtonUp(button: Button, text: String? = null) {
        toggleButton(button, false, text)
    }

    private fun toggleButtonUp(button: Button, @StringRes resourceId: Int) {
        toggleButton(button, false, getString(resourceId))
    }

    private fun toggleButton(button: Button, isDown: Boolean, text: String? = null) {
        if (text != null) button.text = text

        var buttonDrawable = button.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable!!)
        if (isDown) {
            DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.primaryDarkColor))
        } else {
            DrawableCompat.setTint(buttonDrawable, resources.getColor(R.color.primaryColor))
        }
        button.background = buttonDrawable
    }

    private fun requestStreamSettings(identifier: String, feature: PolarBleApi.PolarDeviceDataType)
    : Flowable<PolarSensorSetting> {
        val availableSettings = sensor.api.requestStreamSettings(identifier, feature)
        val allSettings = sensor.api.requestFullStreamSettings(identifier, feature)
            .onErrorReturn { error: Throwable ->
                Log.w(TAG, "Full stream settings are not available for feature $feature. REASON: $error")
                PolarSensorSetting(emptyMap())
            }
        return Single.zip(availableSettings, allSettings) { available: PolarSensorSetting, all: PolarSensorSetting ->
            if (available.settings.isEmpty()) {
                throw Throwable("Settings are not available")
            } else {
                Log.d(TAG, "Feature " + feature + " available settings " + available.settings)
                Log.d(TAG, "Feature " + feature + " all settings " + all.settings)
                return@zip Pair(available, all)
            }
        }
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable()
            .flatMap { sensorSettings: Pair<PolarSensorSetting, PolarSensorSetting> ->
                DialogUtility.showAllSettingsDialog(
                    this@MainActivity,
                    sensorSettings.first.settings,
                    sensorSettings.second.settings
                ).toFlowable()
            }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        toast.show()
    }

    private fun showSnackbar(message: String) {
        val contextView = findViewById<View>(R.id.buttons_container)
        Snackbar.make(contextView, message, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                // Respond to positive button press
            }
            .show()
    }

    private fun disableAllButtons() {
        connectButton.isEnabled = false
        scanButton.isEnabled = false
        accButton.isEnabled = false
        gyrButton.isEnabled = false
        magButton.isEnabled = false
        ppgButton.isEnabled = false
        ppiButton.isEnabled = false
        //Verity Sense recording buttons
        startRecordingButton.isEnabled = false
        stopRecordingButton.isEnabled = false
        downloadRecordingButton.isEnabled = false
        deleteRecordingButton.isEnabled = false
    }

    private fun enableAllButtons() {
        connectButton.isEnabled = true
        scanButton.isEnabled = true
        accButton.isEnabled = true
        gyrButton.isEnabled = true
        magButton.isEnabled = true
        ppgButton.isEnabled = true
        ppiButton.isEnabled = true
        //Verity Sense recording buttons
        startRecordingButton.isEnabled = true
        stopRecordingButton.isEnabled = true
        downloadRecordingButton.isEnabled = true
        deleteRecordingButton.isEnabled = true
    }

    private fun disposeAllStreams() {
        accDisposable?.dispose()
        gyrDisposable?.dispose()
        magDisposable?.dispose()
        ppgDisposable?.dispose()
        ppgDisposable?.dispose()
    }
}