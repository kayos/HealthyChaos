package com.kayos.polar

open class Device(val id: String, val name: String) {
    fun getRecordingsFunctionality(): IRecordingsAPI? {
        return this as? IRecordingsAPI
    }

    fun getStreamFunctionality(): IStreamAPI? {
        return this as? IStreamAPI
    }
}

