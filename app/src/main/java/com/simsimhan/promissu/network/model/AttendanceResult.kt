package com.simsimhan.promissu.network.model

data class AttendanceResult(
        val total : Int,
        val attendance : Int,
        val latecomer : Int,
        val absentee : Int
)