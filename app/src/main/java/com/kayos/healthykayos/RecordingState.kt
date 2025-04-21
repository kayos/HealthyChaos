package com.kayos.healthykayos

sealed class RecordingState {
    class Recording() : RecordingState() {
        override fun toString(): String {
            return "Recording"
        }
    }

    class NotRecording() : RecordingState() {
        override fun toString(): String {
            return "NotRecording"
        }
    }
}