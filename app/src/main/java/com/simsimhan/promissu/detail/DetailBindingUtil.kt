package com.simsimhan.promissu.detail

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.detail.adapter.DetailUserStatusAdapter
import com.simsimhan.promissu.network.model.Participant

@BindingAdapter("setRvItems")
fun setRvItems(recyclerView: RecyclerView, itemList: List<Participant.Response>?) {
    if (itemList != null) {
        (recyclerView.adapter as DetailUserStatusAdapter).setData(itemList)
    } else {
        // TODO 데이터 정보 없음 처리.
    }
}

@BindingAdapter("setBottomSheetIcon")
fun setBottomSheetIcon(imageButton: ImageButton, isSpread: Boolean) {
    if (isSpread) {
        imageButton.setImageResource(R.drawable.ic_arrow_down)
    } else {
        imageButton.setImageResource(R.drawable.ic_arrow_up)
    }
}

@BindingAdapter("setUserItemStatus")
fun setUserItemStatus(imageview: ImageView, status: Int) {
    when (status) {
        0 -> imageview.visibility = View.GONE
        1 -> {
            imageview.visibility = View.VISIBLE
            imageview.setImageResource(R.drawable.ic_icon_status_ing)
        }
        2 -> {
            imageview.visibility = View.VISIBLE
            imageview.setImageResource(R.drawable.ic_icon_status_x)
        }
        3 -> {
            imageview.visibility = View.VISIBLE
            imageview.setImageResource(R.drawable.ic_icon_status_check)
        }
        4 -> {
            imageview.visibility = View.GONE
        }
        else -> {
            imageview.visibility = View.GONE
        }
    }
}