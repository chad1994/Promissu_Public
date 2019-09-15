package com.simsimhan.promissu.ui.main

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.main.adapter.MainAppointmentsAdapter
import com.simsimhan.promissu.ui.main.adapter.MainPendingAppointmentsAdapter
import com.simsimhan.promissu.util.getDayofWeekForKorean
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.util.*

@BindingAdapter("setTodayRvList")
fun setTodayRvList(recyclerView: RecyclerView, list : List<PromiseResponse>?){
    list?.let {
        (recyclerView.adapter as MainAppointmentsAdapter).setData(it)
    }
}

@BindingAdapter("setPendingRvList")
fun setPendingRvList(recyclerView: RecyclerView, list : List<PromiseResponse>?){
    list?.let {
        (recyclerView.adapter as MainPendingAppointmentsAdapter).setData(it)
    }
}

@BindingAdapter("setRemainingTime")
fun setRemainingTime(textView: TextView, dateTime : Date){
    val now = DateTime()
    val promiseStartDate = DateTime(dateTime)
    val hoursDifference = Hours.hoursBetween(now, promiseStartDate)

    if(hoursDifference.hours>=1) {
        textView.text = "${hoursDifference.hours}시간 남음"
    }else{
        val minutesDifference = Minutes.minutesBetween(now,promiseStartDate)
        textView.text = "${minutesDifference.minutes}분 남음"
    }

}


@BindingAdapter("setPendingItemDateTime")
fun setPendingItemDateTime(textView: TextView, dateTime: Date){
    val calendar = Calendar.getInstance()
    calendar.time = dateTime
    textView.text="${calendar.get(Calendar.MONTH)+1}.${calendar.get(Calendar.DATE)} ${getDayofWeekForKorean(calendar.get(Calendar.DAY_OF_WEEK))}"
}


@BindingAdapter("setRemainingDate")
fun setRemainingDate(textView: TextView, dateTime: Date) {
    val now = DateTime()
    val promiseStartDate = DateTime(dateTime)
    val dayDifference = Days.daysBetween(now, promiseStartDate)
    if(dayDifference.days>0) {
        textView.text = "${dayDifference.days}일 남음"
    }else{
        val hourDifference = Hours.hoursBetween(now, promiseStartDate)
        textView.text = "${hourDifference.hours}시간 남음"
    }
}