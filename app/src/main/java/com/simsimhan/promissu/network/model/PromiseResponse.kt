package com.simsimhan.promissu.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromiseResponse(
        @SerializedName("appointment") val promise: Promise.Response,
        @SerializedName("participation") val participation: Participation
) : Parcelable