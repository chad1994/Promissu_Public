package com.simsimhan.promissu.detail

import android.widget.ImageButton
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
