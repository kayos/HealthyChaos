package com.kayos.healthykayos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kayos.polar.Device


@Composable
fun ConnectionScreen(viewModel: ConnectionViewModel = viewModel(factory = ConnectionViewModel.Factory)){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConnectionScreen(
        onSearchClick = { viewModel.search() },
        onConnectClick = { id -> viewModel.connect(id) },
        uiState)
}

@Composable
fun ConnectionScreen(
    onSearchClick: () -> Unit,
    onConnectClick: (deviceId: String) -> Unit,
    uiState: ConnectionUiState
)
{
    Column {
        Button(onClick = onSearchClick, modifier = Modifier.testTag("test-search-btn")) {
            Text("Scan Devices")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Available Devices")
        Column {
            uiState.availableDevices.forEach { device ->
                DeviceItem(device, onClick = { onConnectClick(device.id)})
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Connected Devices")
        if (uiState.connectedDevice != null)
            Device(uiState.connectedDevice)
    }
}

@Composable
private fun Device(device: Device) {
    Box(modifier = Modifier.fillMaxWidth()
        .padding(16.dp)
        .testTag("test-${device.name.lowercase()}-connected-item")
    ) {

        Text(device.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp))
    }
}

@Composable
private fun DeviceItem(device: Device, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onClick)
            .testTag("test-${device.name.lowercase()}-available-item"),
        headlineContent = {
            Text(text = device.name)
        }
    )
}


@Preview
@Composable
fun ConnectionPreview(){
    ConnectionScreen(
        onSearchClick = {  },
        onConnectClick = { },
        ConnectionUiState(listOf(Device("123", "Avail")), Device("234", "Connected"))
    )
}