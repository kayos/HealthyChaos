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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Yellow
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

        Row(
            modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.Center) {

            Text(
                text = "$heartRate",
                style = TextStyle(fontSize = 32.sp, color = Yellow)
            )
            Text(
                text = "bpm",
                style = TextStyle(fontSize = 24.sp, color = Green)
            )
        }

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