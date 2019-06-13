package com.simsimhan.promissu.ui.pastdetail

import android.graphics.Color
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.ui.detail.adapter.DetailUserStatusAdapter
import com.simsimhan.promissu.ui.pastdetail.adapter.DetailPastAdapter

@BindingAdapter("setAttendanceText")
fun setAttendanceText(view: TextView,status: Int){
    when(status){
        2->{
            view.text = "지각했습니다"
        }
        3->{
            view.text = "출석했습니다"
        }
        4->{
            view.text = "결석했습니다"
        }
    }
}

@BindingAdapter("setBackgroundColor")
fun setBackgroundColor(layout: ConstraintLayout, status:Int){
    if(status==4){
        layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_gray)
    }else{
        layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_purple)
    }
}

@BindingAdapter("setIconBackgroundColor")
fun setIconBackgroundColor(view: TextView, status: Int){
    if(status==4){
        view.setBackgroundResource(R.drawable.round_shape_bg_gray_color)
    }else{
        view.setBackgroundResource(R.drawable.round_shape_bg_strong_color)
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

@BindingAdapter("setRankingText","setRankingText2")
fun setRankingText(textView: TextView, ranking: Int, status:Int){
    if(status==4){
        textView.text = "-"
    }else{
        textView.text = "${ranking + 1}위"
    }
}