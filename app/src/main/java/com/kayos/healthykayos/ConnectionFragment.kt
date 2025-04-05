package com.kayos.healthykayos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.kayos.healthykayos.sensor.HeartRateProviderFactory
import com.kayos.healthykayos.sensor.PolarHeartRateSensor

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
                    Connections(sensor)
                }
            }
        }
        return view }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_ConnectionFragment_to_RecordingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
                Text(device.deviceId, color = Color.White)
            }
        }
    }
}