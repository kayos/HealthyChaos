package com.kayos.healthykayos.sensor

import android.content.Context

object HeartRateProviderFactory {
    @JvmStatic
    fun getPolarHeartRateSensor(context: Context): PolarHeartRateSensor {
        return PolarHeartRateSensor.getInstance(context)
    }
}