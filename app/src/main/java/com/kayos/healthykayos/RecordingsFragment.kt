package com.kayos.healthykayos

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.PolarHeartRateSensor
import com.polar.sdk.api.model.PolarOfflineRecordingData
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Calendar

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class RecordingsFragment : Fragment() {

    private val sensor: PolarHeartRateSensor by lazy {
        HeartRateProviderFactory.getPolarHeartRateSensor(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_recordings, container, false)

        val composeView = view.findViewById<ComposeView>(R.id.recordings_compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    RecordingsScreen(sensor, sensor.selectedDeviceId!!)
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_RecordingsFragment_to_ConnectionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}


@Composable
fun RecordingsScreen(sensor: PolarHeartRateSensor, deviceId: String) {
    val recordings = sensor.recordings.collectAsState().value.sortedBy { entry -> entry.date }
    val isRecording = remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun saveDataToCSV(data : PolarOfflineRecordingData.HrOfflineRecording) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "polar-${data.startTime.time}.csv")
            val fileWriter: FileWriter
            val bufferedWriter: BufferedWriter
            try {
                fileWriter = FileWriter(filePath)
                bufferedWriter = BufferedWriter(fileWriter)

                bufferedWriter.write("time,hr,correctedHr")
                bufferedWriter.newLine()

                var timestamp = data.startTime
                for (sample in data.data.samples) {
                    timestamp.add(Calendar.SECOND, sensor.hrSampleRateSec)
                    bufferedWriter.write("${timestamp.time},${sample.hr},${sample.correctedHr}")
                    bufferedWriter.newLine()
                }
                bufferedWriter.flush()
                bufferedWriter.close()
                Log.d("RecordingsFragment", "Done saving to file: $filePath")
            } catch (e: IOException) {
                Log.e("RecordingsFragment", "Error saving. ${e.message}")
            }
        } else {
            Log.e("RecordingsFragment", "External storage is not available")
        }
    }

    // TODO start stop is leaking subscriptions - FIX
    fun startRecording(selectedDeviceId: String) {
        sensor.startRecording(selectedDeviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                isRecording.value = true
            }
            .doOnError {
                isRecording.value = false
            }
            .subscribe(
                { },
                {
                    throwable ->
                    //TODO fix logic around isRecording
                    isRecording.value = true
                    throwable.printStackTrace()
            })
    }

    fun stopRecording(selectedDeviceId: String) {
        sensor.stopRecording(selectedDeviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                isRecording.value = false
            }
            .doOnError {
                isRecording.value = true
            }
            .subscribe(
                { },
                { throwable ->
                    // Handle any error here explicitly
                    isRecording.value = false
                    // Log or process the error as needed
                    throwable.printStackTrace() // Or use any other error logging mechanism
                })
    }

    fun download(recording: PolarOfflineRecordingEntry) {
         sensor.downloadRecording(sensor.selectedDeviceId!!, recording)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { data: PolarOfflineRecordingData ->
                    when (data) {
                        is PolarOfflineRecordingData.HrOfflineRecording -> {
                            saveDataToCSV(data)
                        }
                        else -> {
                            Log.d("RecordingsFragment", "Recording type is not yet implemented")
                        }
                    }
                },
                { throwable ->
                    throwable.printStackTrace()
                })
    }

    Column {
        Button(
            onClick = { if (isRecording.value) stopRecording(sensor.selectedDeviceId!!) else startRecording(sensor.selectedDeviceId!!) },
            modifier = Modifier
                .padding(bottom = 8.dp)
                .testTag("test-record-btn")
        ) {
            Text(text = if (isRecording.value) "Stop Recording" else "Start Recording")
        }

        Text(text = if (isRecording.value) "Recording..." else "Not Recording")

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Button(modifier = Modifier.testTag("test-refresh-recordings-btn"),
            onClick = {
            sensor.listRecordings(deviceId)
        }) {
            Text("Refresh")
        }
        LazyColumn {
            items(recordings){ recording ->
                RecordingItem(
                    recording,
                    onDownloadClick = {
                        download(recording)
                    },
                    onDeleteClick = {
                        sensor.deleteRecording(sensor.selectedDeviceId!!, recording)
                    },
                    itemModifier = Modifier.testTag("test-reocringd-item-${recording.date.toString()}")
                )
            }
        }
    }
}

@Composable
fun RecordingItem(
    recording: PolarOfflineRecordingEntry,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit,
    itemModifier: Modifier,) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ListItem(
                modifier = itemModifier.padding(4.dp),
                headlineContent = {
                    Text(
                        text = recording.date.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                supportingContent = {
                    Text(
                        text = "Size: ${recording.size} \nType: ${recording.type}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                trailingContent = {
                    Row{
                    IconButton(onClick = onDownloadClick) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Download")
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    }
                }
            )
        }
    }
}