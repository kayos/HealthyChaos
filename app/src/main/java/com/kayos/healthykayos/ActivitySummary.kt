package com.kayos.healthykayos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kayos.device.HrData
import kotlin.time.Duration.Companion.seconds


@Composable
fun ActivitySummary(samples: List<HrData.HrSample>, modifier: Modifier = Modifier) {
    val avgBpm = samples.map { it.bpm }.average()
    val duration = if (samples.isNotEmpty()) samples.last().secondsFromStart.seconds else 0.seconds
    val age = 30 // TODO: Replace with actual user age
    val bodyWeight = 70 // TODO: Replace with actual user weight in kg

    //TODO: This is supposedly for women, is it accurate? Extend for men.
    val caloriesBurned =  duration.inWholeMinutes * (0.4472*avgBpm - 0.1263* bodyWeight + 0.074 * age - 20.4022) / 4.184

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SummaryItem(
                title = "Total time",
                value = duration.toComponents { hour, minute, second, _ ->
                    "%d:%02d:%02d".format(hour, minute, second)
                }
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SummaryItem(
                title = "Average bpm",
                value = avgBpm.toInt().toString()
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SummaryItem(
                title = "Calories burned",
                value = "%,.0f kcal".format(caloriesBurned)
            )
        }
    }
}

@Composable
fun SummaryItem(
    title : String,
    value: String,
    modifier: Modifier = Modifier
) {
    Text(title, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
    Text(value)
}

@Preview(showBackground = true)
@Composable
fun ActivitySummaryPreview() {
    val samples = listOf(
        HrData.HrSample(bpm = 80, secondsFromStart = 0),
        HrData.HrSample(bpm = 115, secondsFromStart = 1),
        HrData.HrSample(bpm = 190, secondsFromStart = 300),
        HrData.HrSample(bpm = 140, secondsFromStart = 600)
    )

    ActivitySummary(samples)
}