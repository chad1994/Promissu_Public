package com.simsimhan.promissu.ui.invinting

import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.ui.invinting.adapter.InvitingParticipantAdapter

@BindingAdapter("setInvitingParticipantRvItems")
fun setInvitingParticipantRvItems(recyclerView: RecyclerView, list: List<Participant.Response>?) {
    list?.let {
        (recyclerView.adapter as InvitingParticipantAdapter).setData(it)
    }
}

@BindingAdapter("setInvitingParticipantImage")
fun setInvitingParticipantImage(imageView: ImageView, url: String?) {
//    if(url.isNullOrBlank()){
//        imageView.setImageResource(R.drawable.ic_launcher_foreground)
//    }else{
    url?.let {
        val options = RequestOptions()
                .circleCrop()
                .error(R.drawable.ic_icon_emptyimg)
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        imageView.scaleType = ImageView.ScaleType.CENTER

        Glide.with(imageView.context)
                .load(url)
                .apply(options)
                .into(imageView)
    }
}

@BindingAdapter("setBackgroundColor")
fun setBackgroundColor(layout: ConstraintLayout, id: Int) {
    if (id == PromissuApplication.diskCache!!.userId.toInt()) {
        layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_purple_mine)
    } else {
        layout.setBackgroundResource(R.drawable.bg_circle_detail_past_item_purple)
    }
}

@BindingAdapter("setInvitingConfirmButton")
fun setInvitingConfirmButton(textView: TextView, admin_id: Long) {
    if (admin_id == PromissuApplication.diskCache?.userId) {
        textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.main_03))
        textView.text = textView.context.getText(R.string.inviting_confirm_button_text_admin)
        textView.isClickable = true
    } else {
        textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.grey_02))
        textView.text = textView.context.getText(R.string.inviting_confirm_button_text_not_admin)
        textView.isClickable = false
    }
}
