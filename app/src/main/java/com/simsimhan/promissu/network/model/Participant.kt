package com.simsimhan.promissu.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


class Participant {
    @Parcelize
    data class Response(
            val kakao_id: Int,
            val nickname: String,
            val participation: Int,
            val updatedAt: Date,
            val status : Int = 0,
            val profile_url : String
    ):Parcelable

    @Parcelize
    data class Request(
            val kakao_id: Int,
            val nickname: String,
            val participation: Int,
            val updatedAt: Date,
            val status : Int = 0,
            val profile_url : String
    ):Parcelable


    class CompareByStatus : Comparator<Response> {

        override fun compare(o1: Response, o2: Response): Int {
            return if (o1.status > o2.status)
                1
            else if (o1.status < o2.status)
                -1
            else {
                if (o1.updatedAt.after(o2.updatedAt)) {
                    1
                } else {
                    -1
                }
            }
        }
    }

}