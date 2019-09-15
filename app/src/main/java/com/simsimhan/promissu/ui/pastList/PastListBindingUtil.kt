package com.simsimhan.promissu.ui.pastList

import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.pastList.adapter.PastListAdapter
import com.simsimhan.promissu.util.getDayofWeekForKorean
import java.util.*

@BindingAdapter("setPastListRvItems")
fun setPastListRvItems(recyclerView: RecyclerView, itemList: List<PromiseResponse>?) {
    itemList?.let {
        (recyclerView.adapter as PastListAdapter).setData(it)
    }
}

@BindingAdapter("setPastListItemBg")
fun setPastListItemBg(layout : ConstraintLayout, status: Int ){
    if(status == 2){
        layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_purple)
    }else{
        layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_gray)
    }
}

@BindingAdapter("setPastListItemButtonBg")
fun setPastListItemButtonBg(button : Button , status : Int){
    if(status == 2){
        button.text = "출석"
        button.setBackgroundResource(R.drawable.round_shape_bg_strong_color)
    }else if( status == 3){
        button.text = "지각"
        button.setBackgroundResource(R.drawable.round_shape_bg_gray_color)
    }else{
        button.text = "결석"
        button.setBackgroundResource(R.drawable.round_shape_bg_gray_color)
    }

}

@BindingAdapter("setPastListItemDateTime")
fun setPastListItemDateTime(textView: TextView, dateTime : Date){
    val calendar = Calendar.getInstance()
    calendar.time = dateTime
    textView.text="${calendar.get(Calendar.MONTH)+1}.${calendar.get(Calendar.DATE)} ${getDayofWeekForKorean(calendar.get(Calendar.DAY_OF_WEEK))}"
}
