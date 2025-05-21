package com.kayos.healthykayos

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.polar.sdk.api.model.PolarOfflineRecordingEntry
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer

@Composable
fun RecordingsScreen(
    viewModel: RecordingsViewModel = viewModel(factory = RecordingsViewModel.Factory))
{
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RecordingsScreen(
        uiState.recordings,
        uiState.recordingStatus,
        onStartRecordingClick = { viewModel.startRecording() },
        onStopRecordingClick = { viewModel.stopRecording() },
        onDownloadClick = { recording: PolarOfflineRecordingEntry, writer: Writer ->
            viewModel.download(recording, writer)
        },
        onDeleteClick = { recording: PolarOfflineRecordingEntry ->
            viewModel.deleteRecording(recording)
        },
        onRefreshClick = { viewModel.refreshRecordings() }
    )
}

@Composable
fun RecordingsScreen(
    recordings: List<PolarOfflineRecordingEntry>,
    isRecording: RecordingState,
    onStartRecordingClick: () -> Unit,
    onStopRecordingClick: () -> Unit,
    onDownloadClick: (PolarOfflineRecordingEntry, Writer) -> Unit,
    onDeleteClick: (PolarOfflineRecordingEntry) -> Unit,
    onRefreshClick: () -> Unit,
)
{

    val context = LocalContext.current

    Column {
        if (isRecording is RecordingState.Recording)
        {
            Button(
                onClick = onStopRecordingClick,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("test-stop-record-btn")
            ) {
                Text(text = "Stop Recording")
            }
        } else {
            Button(
                onClick = onStartRecordingClick,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("test-start-record-btn")
            ) {
                Text(text = "Start Recording")
            }
        }


        Text(text = if (isRecording is RecordingState.Recording) "Recording..." else "Not Recording")

        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(
            onClick = onRefreshClick,
            modifier = Modifier.testTag("test-refresh-recordings-btn")) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
        }
        LazyColumn {
            itemsIndexed(recordings){ index, recording ->
                RecordingItem(
                    recording,
                    index,
                    onDownloadClick = {
                        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                            val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "polar-${recording.date.time}.csv")
                            val fileWriter = FileWriter(filePath)
                            val bufferedWriter = BufferedWriter(fileWriter)
                            onDownloadClick(recording, bufferedWriter)
                        }},
                    onDeleteClick = { onDeleteClick(recording) }
                )
            }
        }
    }
}

@Composable
fun RecordingItem(
    recording: PolarOfflineRecordingEntry,
    index: Int,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit) {

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
                modifier = Modifier
                    .padding(4.dp)
                    .testTag("test-recording-item-${index}"),
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
                    IconButton(
                        onClick = onDownloadClick,
                        modifier = Modifier.testTag("test-recording-item-${index}-download-btn")) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Download")
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .testTag("test-recording-item-${index}-delete-btn")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                    }
                }
            )
        }
    }
}