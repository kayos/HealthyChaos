package com.kayos.polar

import kotlinx.coroutines.flow.Flow

interface IStreamAPI {
    fun startStream(): Flow<HeartRate>
}
