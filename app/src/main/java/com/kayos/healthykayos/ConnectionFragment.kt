package com.kayos.healthykayos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.PolarHeartRateSensor
import com.polar.sdk.api.model.PolarDeviceInfo

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ConnectionFragment : Fragment() {

    private val sensor: PolarHeartRateSensor by lazy {
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
                    Column(modifier = Modifier.padding(16.dp)){
                        Connections(
                            sensor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Navigation(findNavController())
                    }

                }
            }
        }
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}

@Composable
fun Navigation(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            navController.navigate(R.id.action_ConnectionFragment_to_RecordingsFragment)
        }) {
            Text("Recording")
        }
    }
}


@Composable
fun Connections(sensor: PolarHeartRateSensor) {
    val availableDevices = sensor.availableDevices.collectAsState().value
    Column {
        Button(onClick = {
            sensor.search()
        }) {
            Text("Scan Devices")
        }
        Column {
            availableDevices.forEach { device ->
                DeviceItem(device, onClick =  { sensor.connect(device) })
            }
        }
    }
}

@Composable
fun DeviceItem(device: PolarDeviceInfo, onClick: () -> Unit) {
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
                Text(text = "Id: ${device.deviceId} | Address: ${device.address}", style = MaterialTheme.typography.bodyMedium)
            }
        )
    }
}