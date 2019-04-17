package com.simsimhan.promissu.network.model

import java.util.*

data class LocationEvent(
        val apptId : Int,
        val id: Int,
        val lat: Double,
        val lon: Double,
        val partId: Int,
        val point: Int,
        val nickname : String,
        val status: Int,
        val timestamp: Date
)