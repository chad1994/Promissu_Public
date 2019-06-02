package com.simsimhan.promissu.network.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Appointment(
        @SerializedName("appointment")val promise: Promise.Response,
        val status : Int
) : Parcelable