package com.simsimhan.promissu.util

fun getDayofWeekForKorean(day_of_week : Int) : String{
    return when(day_of_week){
        1 -> "일"
        2 -> "월"
        3 -> "화"
        4 -> "수"
        5 -> "목"
        6 -> "금"
        7 -> "토"
        else -> "일"
    }
}