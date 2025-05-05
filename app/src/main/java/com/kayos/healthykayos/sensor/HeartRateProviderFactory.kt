package com.kayos.healthykayos.sensor

import android.content.Context
import com.kayos.polar.PolarHeartRateSensor

object HeartRateProviderFactory {
    @JvmStatic
    fun getPolarHeartRateSensor(context: Context): PolarHeartRateSensor {
        return PolarHeartRateSensor.getInstance(context)
    }
}