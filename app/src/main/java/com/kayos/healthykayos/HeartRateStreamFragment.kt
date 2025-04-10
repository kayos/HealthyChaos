package com.kayos.healthykayos

import androidx.compose.runtime.getValue
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kayos.healthykayos.sensor.HeartRate

class HeartRateStreamFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LiveHeartRateScreen()
            }
        }
    }
}

@Composable
private fun LiveHeartRateScreen(
    viewModel: LiveHeartRateViewModel = viewModel(factory = LiveHeartRateViewModel.Factory)
) {

    val heartRate by viewModel.heartRate.collectAsStateWithLifecycle()

    LiveHeartRateScreen(
        sample = heartRate,
        onStartMonitoringClick = { viewModel.startStreaming() },
        onStopMonitoringClick = { viewModel.stopStreaming() }
    )
}

@Composable
fun LiveHeartRateScreen(
    sample: HeartRate?,
    onStartMonitoringClick: () -> Unit,
    onStopMonitoringClick: () -> Unit)
{

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (sample != null) {
                Text(
                    text = "${sample?.bpm}",
                    style = TextStyle(fontSize = 32.sp, color = Yellow),
                    modifier = Modifier.testTag("test-hr-text")
                )
                Text(
                    text = "bpm",
                    style = TextStyle(fontSize = 24.sp, color = Green),
                    modifier = Modifier.testTag("test-bpm-text")
                )
            }

        }

        Spacer(modifier = Modifier.padding(8.dp))

        var isMonitoring = remember { mutableStateOf(false) }

        if (isMonitoring.value) {
            Button(
                onClick = {
                    onStopMonitoringClick()
                    isMonitoring.value = false
                },
                modifier = Modifier.testTag("test-stop-monitoring-btn")
            ) {
                Text("Stop monitoring")
            }
        }
        else{
            Button(onClick = {
                    onStartMonitoringClick()
                    isMonitoring.value = true
                 },
                modifier = Modifier.testTag("test-start-monitoring-btn")) {
                Text("Start monitoring")
            }
        }
    }
}
