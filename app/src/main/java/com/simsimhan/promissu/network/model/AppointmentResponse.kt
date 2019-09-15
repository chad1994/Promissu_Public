package com.simsimhan.promissu.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppointmentResponse(
        val data : List<PromiseResponse>,
        val total : Int
): Parcelable