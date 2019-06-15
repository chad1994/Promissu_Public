package com.simsimhan.promissu.ui.pastdetail

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.ui.pastdetail.adapter.DetailPastAdapter
import org.joda.time.DateTime
import org.joda.time.Minutes
import java.util.*

@BindingAdapter("setAttendanceText")
fun setAttendanceText(view: TextView, status: Int) {
    when (status) {
        2 -> {
            view.text = "지각했습니다"
        }
        3 -> {
            view.text = "출석했습니다"
        }
        4 -> {
            view.text = "결석했습니다"
        }
    }
}

@BindingAdapter("setBackgroundColor", "setBackgroundColor2")
fun setBackgroundColor(layout: ConstraintLayout, status: Int, id: Int) {
    if (status == 3) {
        if (id == PromissuApplication.diskCache!!.userId.toInt()) {
            layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_purple_mine)
        } else {
            layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_purple)
        }
    } else {
        if (id == PromissuApplication.diskCache!!.userId.toInt()) {
            layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_gray_mine)
        } else {
            layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_gray)
        }
    }
}

@BindingAdapter("setIconBackgroundColor")
fun setIconBackgroundColor(view: TextView, status: Int) {
    if (status == 3) {
        view.setBackgroundResource(R.drawable.round_shape_bg_strong_color)
    } else {
        view.setBackgroundResource(R.drawable.round_shape_bg_gray_color)
    }
}

@BindingAdapter("setDetailPastItems")
fun setDetailPastItems(recyclerView: RecyclerView, itemList: List<Participant.Response>?) {
    if (itemList != null) {
        (recyclerView.adapter as DetailPastAdapter).setData(itemList)
    } else {
        // TODO 데이터 정보 없음 처리.
    }
}

@BindingAdapter("setRankingText", "setRankingText2")
fun setRankingText(textView: TextView, ranking: Int, status: Int) {
    if (status == 4) {
        textView.text = "-"
    } else {
        textView.text = "${ranking + 1}위"
    }
}

@BindingAdapter("setRankingAbsent")
fun setRankingAbsent(textView: TextView, status: Int) {
    if (status == 4) {
        textView.visibility = View.VISIBLE
    } else {
        textView.visibility = View.GONE
    }
}

@BindingAdapter("setRankingAttendance")
fun setRankingAttendance(textView: TextView, status: Int) {
    when (status) {
        2 -> {
            textView.visibility = View.VISIBLE
            textView.text = "지각"
        }
        3 -> {
            textView.visibility = View.VISIBLE
            textView.text = "출석"
        }
        4 -> textView.visibility = View.GONE
    }
}

@BindingAdapter("setRankingTime", "setRankingTime2", "setRankingTime3", requireAll = false)
fun setRankingTime(textView: TextView, status: Int?, updatedAt: Date?, appointmentTime: Date?) {
    if (status == 4) {
        textView.visibility = View.GONE
    } else {
        textView.visibility = View.VISIBLE
        val updateTime = DateTime(updatedAt)
        val startTime = DateTime(appointmentTime)
        val minDiff = Minutes.minutesBetween(updateTime, startTime)
        if (status == 2) {
            textView.text = "${Math.abs(minDiff.minutes)}분"
            textView.setTextColor(textView.context.resources.getColor(R.color.black))
        } else if (status == 3) {
            textView.text = "${Math.abs(minDiff.minutes)}분 빨리"
            textView.setTextColor(textView.context.resources.getColor(R.color.main_01))
        }
    }
}