package com.kayos.healthykayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class HeartRateStreamFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LiveHeartRateScreen(34)
            }
        }
    }
}

@Composable
fun LiveHeartRateScreen(heartRate: Int){
    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.padding(8.dp))

  //      if (heartRate != null) {
            Text(text = "Heart Rate: $heartRate bpm", style = TextStyle(fontSize = 24.sp))
//        } else {
//            Text(text = "Waiting for heart rate data...", style = TextStyle(fontSize = 18.sp))
//        }

        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = { /* Add actions like connecting or pausing */ }) {
            Text("Start Monitoring")
        }
    }
}

@Preview
@Composable
fun PreviewHeartRateScreen() {
    LiveHeartRateScreen(heartRate = 72)
}