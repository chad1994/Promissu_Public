package com.simsimhan.promissu.ui.promise

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.network.model.Promise
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes

@BindingAdapter("setItemLeftTime1", "setItemLeftTime2")
fun setItemLeftTime(text: TextView, isPast: Boolean, response: Promise.Response) {
    if (isPast) {
        text.visibility = View.GONE
    } else {
        text.visibility = View.VISIBLE
        val now = DateTime()
        val promiseStartDate = DateTime(response.datetime)
        val daysDifference = Days.daysBetween(now, promiseStartDate)

        if (daysDifference.days == 0) {
            val hoursDifference = Hours.hoursBetween(now, promiseStartDate)
            val minuteDifference = Minutes.minutesBetween(now, promiseStartDate)
            if (hoursDifference.hours < 1) {
                if (response.status == 1) {
                    text.text = ""
                } else {
                    text.text = minuteDifference.minutes.toString()
                }
            } else {
                text.text = hoursDifference.hours.toString()
            }
        } else {
            text.text = daysDifference.days.toString()
        }
    }
}

@BindingAdapter("setItemLeftLabel1", "setItemLeftLabel2")
fun setItemLeftLabel(text: TextView, isPast: Boolean, response: PromiseResponse) {
    if (isPast) {
        when (response.participation.status) {
            2 -> {
                text.setTextColor(ContextCompat.getColor(text.context, R.color.sub_color))
                text.text = "출석"
            }
            3 -> {
                text.setTextColor(ContextCompat.getColor(text.context, R.color.sub_color))
                text.text = "지각"
            }
            4 -> {
                text.setTextColor(ContextCompat.getColor(text.context, R.color.sub_color))
                text.text = "결석"
            }
            -1 -> {
                text.setTextColor(ContextCompat.getColor(text.context, R.color.sub_color))
                text.text = "삭제된 약속"
            }
        }

    } else {
        val now = DateTime()
        val promiseStartDate = DateTime(response.promise.datetime)
        val daysDifference = Days.daysBetween(now, promiseStartDate)

        if (daysDifference.days == 0) {
            val hoursDifference = Hours.hoursBetween(now, promiseStartDate)
            if (hoursDifference.hours < 1) {
                if (response.promise.status == 1) {
                    text.text = "출석하세요!"
                    text.setTextColor(ContextCompat.getColor(text.context, R.color.sub_color))
                } else {
                    text.text = "분 남음"
                    text.setTextColor(ContextCompat.getColor(text.context, R.color.black))
                }
            } else {
                text.text = "시간 남음"
                text.setTextColor(ContextCompat.getColor(text.context, R.color.black))
            }
        } else {
            text.text = "일 남음"
            text.setTextColor(ContextCompat.getColor(text.context, R.color.black))
        }
    }
}

@BindingAdapter("setContainerBg")
fun setContainerBg(container: ConstraintLayout, isPast: Boolean) {
    if (isPast) {
        container.setBackgroundColor(ContextCompat.getColor(container.context, R.color.past_background_color))
    } else {
        container.setBackgroundColor(ContextCompat.getColor(container.context, R.color.background_grey))
    }
}

@BindingAdapter("setAdminIconVisibility")
fun setAdminIconVisibility(view: View, id: Long) {
    if (id == PromissuApplication.diskCache!!.userId) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}