package com.simsimhan.promissu.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Participation(
        val id : Int,
        val status : Int
) : Parcelable