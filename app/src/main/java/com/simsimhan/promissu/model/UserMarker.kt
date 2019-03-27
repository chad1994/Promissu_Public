package com.simsimhan.promissu.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng


data class UserMarker (
        val name: String,
        val landCode: LatLng // pnu
)