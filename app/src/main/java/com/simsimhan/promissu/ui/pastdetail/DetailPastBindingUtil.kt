package com.simsimhan.promissu.ui.pastdetail

import android.widget.TextView
import androidx.databinding.BindingAdapter

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