package com.kayos.healthykayos

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

class HeartRateStreamFragment : Fragment() {

    private val viewModel: LiveHeartRateViewModel by viewModels {
        LiveHeartRateViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LiveHeartRateScreen(viewModel)
            }
        }
    }
}

@Composable
fun LiveHeartRateScreen(viewModel: LiveHeartRateViewModel)
{
    val sample = viewModel.heartRate.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (sample.value != null) {
                Text(
                    text = "${sample.value?.bpm}",
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

        if (sample.value == null) {
            Button(onClick = { viewModel.startStreaming() }) {
                Text("Start Monitoring")
            }
        } else {
            Button(onClick = { viewModel.stopStreaming() }) {
                Text("Stop Monitoring")
            }
        }
    }
}
