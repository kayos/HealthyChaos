package com.kayos.healthykayos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
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
                    ConnectionScreen(
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
fun ConnectionScreen(sensor: IHeartRateSensor,
                     onRecordingsClick: () -> Unit,
                     onLiveClick: () -> Unit,
                     viewModel: ConnectionViewModel = viewModel(factory = ConnectionViewModel.Factory)){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConnectionScreen(sensor, onRecordingsClick, onLiveClick, onSearchClick = {viewModel.search()}, uiState)
}
