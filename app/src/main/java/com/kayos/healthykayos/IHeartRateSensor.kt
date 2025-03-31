package com.kayos.healthykayos

import com.polar.sdk.api.model.PolarDeviceInfo
import io.reactivex.rxjava3.core.Flowable

interface IHeartRateSensor {
    fun search(): Flowable<PolarDeviceInfo>
    fun connect(id: String): Unit
    fun disconnect(id: String): Unit
}
