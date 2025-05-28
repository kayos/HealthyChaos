package com.kayos.device

import kotlinx.coroutines.flow.Flow

interface IStreamAPI {
    fun startStream(): Flow<HeartRate>
}