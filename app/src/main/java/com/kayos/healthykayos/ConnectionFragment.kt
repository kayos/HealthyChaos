package com.kayos.healthykayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.kayos.polar.Device
import com.kayos.polar.HeartRateProviderFactory
import com.kayos.polar.IHeartRateSensor

class ConnectionFragment : Fragment() {

    private val sensor: IHeartRateSensor by lazy {
        HeartRateProviderFactory.getPolarHeartRateSensor(requireActivity().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_connection, container, false)

        val composeView = view.findViewById<ComposeView>(R.id.connection_compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Connections(
                        sensor,
                        onRecordingsClick = {
                            findNavController().navigate(R.id.action_ConnectionFragment_to_RecordingsFragment)
                        },
                        onLiveClick = {
                            findNavController().navigate(R.id.action_ConnectionFragment_to_HeartRateStreamFragment)
                        },
                        viewModel = viewModel(factory = ConnectionViewModel.Factory)
                    )
                }
            }
        }
        return view
    }

}

@Composable
fun Connections(sensor: IHeartRateSensor,
                onRecordingsClick: () -> Unit,
                onLiveClick: () -> Unit,
                viewModel: ConnectionViewModel = viewModel(factory = ConnectionViewModel.Factory)){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Connections(sensor, onRecordingsClick, onLiveClick, uiState)
}

@Composable
fun Connections(
    sensor: IHeartRateSensor,
    onRecordingsClick: () -> Unit,
    onLiveClick: () -> Unit,
    uiState: ConnectionUiState
)
{
    val connectedDevice = sensor.connectedDevices.collectAsState().value

    Column {
        Button(onClick = {
            sensor.search()
        }) {
            Text("Scan Devices")
        }
        Column {
            uiState.availableDevices.forEach { device ->
                DeviceItem(device, onClick = { sensor.connect(device.id) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (connectedDevice != null)
            Device(Device(connectedDevice.deviceId, connectedDevice.name), onRecordingsClick, onLiveClick)
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