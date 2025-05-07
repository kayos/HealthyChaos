@file:OptIn(ExperimentalMaterial3Api::class)

package com.kayos.healthykayos

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kayos.polar.Device

@Composable
internal fun DeviceBar(viewModel: DeviceBarViewModel = DeviceBarViewModel()) {
    val device by viewModel.connectedDevice.collectAsStateWithLifecycle()

    DeviceBar(device = device)
}

@Composable
internal fun DeviceBar(device: Device?){
    TopAppBar(
        title = {
            if(device == null){
                Text("No Device", modifier = Modifier.testTag("test-no-device-text"))
            }
        },
        actions = {
            IconButton(onClick = {
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Connect Device")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.DarkGray,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Preview
@Composable
private fun NoDevicePreview(){
    DeviceBar()
}
