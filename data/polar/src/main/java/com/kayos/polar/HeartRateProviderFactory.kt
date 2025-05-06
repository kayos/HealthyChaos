package com.kayos.polar

import android.content.Context

object HeartRateProviderFactory {
    @JvmStatic
    fun getPolarHeartRateSensor(context: Context): IHeartRateSensor {
        return PolarHeartRateSensor.getInstance(context)
    }
}