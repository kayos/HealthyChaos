package com.kayos.polar

import android.content.Context

object HeartRateProviderFactory {
    @JvmStatic
    fun getPolarHeartRateSensor(context: Context): PolarHeartRateSensor {
        return PolarHeartRateSensor.getInstance(context)
    }
}