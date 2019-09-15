package com.simsimhan.promissu.ui.invitingList

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.invitingList.adapter.InvitingListAdapter
import com.simsimhan.promissu.util.getDayofWeekForKorean
import java.util.*


@BindingAdapter("setRvItems")
fun setRvItems(recyclerView: RecyclerView, itemList: List<PromiseResponse>?) {
    itemList?.let {
        (recyclerView.adapter as InvitingListAdapter).setData(it)
    }
}

@BindingAdapter("setDepositVisibility")
fun setDepositVisibility(textView: TextView, deposit : String?){
    if(deposit.isNullOrEmpty()){
        textView.visibility = View.GONE
    }else{
        textView.visibility = View.VISIBLE
    }
}

@BindingAdapter("setInvitingListText")
fun setInvitingListText(textView : TextView, itemList: List<PromiseResponse>?){
    if(itemList.isNullOrEmpty()){
        textView.text = textView.context.getString(R.string.inviting_list_empty_text)
    }else{
        textView.text = textView.context.getString(R.string.inviting_list_not_empty_text)
    }
}

@BindingAdapter("setInvitingListItemDateTime")
fun setInvitingListItemDateTime(textView: TextView, dateTime: Date){
    val calendar = Calendar.getInstance()
    calendar.time = dateTime
    textView.text="${calendar.get(Calendar.MONTH)+1}.${calendar.get(Calendar.DATE)} ${getDayofWeekForKorean(calendar.get(Calendar.DAY_OF_WEEK))}"
}