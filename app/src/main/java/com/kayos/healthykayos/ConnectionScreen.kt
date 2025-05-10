package com.kayos.healthykayos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kayos.polar.Device
import com.kayos.polar.IHeartRateSensor


@Composable
fun ConnectionScreen(
    sensor: IHeartRateSensor,
    onRecordingsClick: () -> Unit,
    onLiveClick: () -> Unit,
    onSearchClick: () -> Unit,
    onConnectClick: (deviceId: String) -> Unit,
    uiState: ConnectionUiState
)
{
    Column {
        Button(onClick = onSearchClick) {
            Text("Scan Devices")
        }
        Column {
            uiState.availableDevices.forEach { device ->
                DeviceItem(device, onClick = { onConnectClick(device.id)})
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.connectedDevice != null)
            Device(uiState.connectedDevice, onRecordingsClick, onLiveClick)
    }
}

@Composable
fun Device(device: Device, onRecordingsClick: () -> Unit, onLiveClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(Color.LightGray)) {

        Text("Sensor: ${device.id}", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Button(
                onClick = onRecordingsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recordings")
            }
            Button(
                onClick = onLiveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Live HR")
            }
        }

    }
}

@Composable
fun DeviceItem(device: Device, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        ListItem(
            modifier = Modifier.padding(16.dp),
            headlineContent = {
                Text(text = device.name, style = MaterialTheme.typography.bodyLarge)
            },
            supportingContent = {
                Text(text = "Id: ${device.id}", style = MaterialTheme.typography.bodyMedium)
            }
        )
    }
}
