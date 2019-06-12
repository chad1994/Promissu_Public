package com.simsimhan.promissu.ui.pastdetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
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


@BindingAdapter("setDetailPastItems")
fun setDetailPastItems(recyclerView: RecyclerView, itemList: List<Participant.Response>?) {
    if (itemList != null) {
        (recyclerView.adapter as DetailPastAdapter).setData(itemList)
    } else {
        // TODO 데이터 정보 없음 처리.
    }
}