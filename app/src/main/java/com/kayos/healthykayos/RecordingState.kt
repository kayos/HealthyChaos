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

    companion object {
        fun determineState(isRecording: Boolean) : RecordingState{
            return if (isRecording) Recording() else NotRecording()
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (
                this::class == other?.let { it::class }
                )
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}