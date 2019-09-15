package com.simsimhan.promissu.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Promise : Parcelable {

    @Parcelize
    data class Response(
            val id : Int,
            val title : String,
            val description : String,
            val deposit : String?,
            val datetime : Date,
            val location : String,
            val location_name : String,
            val location_lon : Double,
            val location_lat : Double,
            val late_range : Int,
            val admin_id : Long,
            val status : Int,
            val createdAt : Date,
            val updatedAt : Date
    ) : Parcelable


    @Parcelize
    data class Request(
            val title : String,
            val description: String,
            val datetime : String,
            val location : String,
            val location_name : String,
            val location_lat : Double,
            val location_lon : Double,
            val late_range : Int
    ): Parcelable
}