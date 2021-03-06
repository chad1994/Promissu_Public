package com.simsimhan.promissu.ui.detail

import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.ui.detail.adapter.DetailAttendanceAdapter
import com.simsimhan.promissu.ui.detail.adapter.DetailUserStatusAdapter


@BindingAdapter("setRvItems")
fun setRvItems(recyclerView: RecyclerView, itemList: List<Participant.Response>?) {
    itemList?.let {
        (recyclerView.adapter as DetailUserStatusAdapter).setData(it)
    }
}

@BindingAdapter("detailEmptyTextVisible")
fun detailEmptyTextVisible(text: TextView, list: List<Participant.Response>) {
    if (list.isEmpty()) {
        text.visibility = View.VISIBLE
    } else {
        text.visibility = View.GONE
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
        0 -> {
            imageview.visibility = View.GONE
        }
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
            imageview.visibility = View.VISIBLE
            imageview.setImageResource(R.drawable.ic_icon_attend_complete)
        }
        else -> {
            imageview.visibility = View.GONE
        }
    }
}

@BindingAdapter("setUserItemAttended")
fun setUserItemAttended(text: TextView, status: Int) {
    when (status) {
        4 -> text.visibility = View.VISIBLE
        else -> text.visibility = View.GONE
    }
}

@BindingAdapter("visibilityByArrive", "visibilityByArrive2", "visibilityByArrive3")
fun visibilityByArrive(text: TextView, visibility: Boolean, attended: Boolean, isSocketOpen: Boolean) {
    if (text.id == R.id.detail_attend_button) {
        if (visibility && !attended && isSocketOpen) {
            text.visibility = View.VISIBLE
        } else {
            text.visibility = View.GONE
        }
    } else {
        if ((visibility && !attended) || !isSocketOpen) {
            text.visibility = View.GONE
        } else {
            text.visibility = View.VISIBLE
        }
    }
}

var handlerState = false
var handlerCount = 0
var timeCount = 0
@BindingAdapter("setLottieAnim")
fun setLottieAnim(lottie: LottieAnimationView, anim_status: Int) {
    val handler = Handler()
    val runnableState = Runnable {
        run {
            timeCount++
        }
    }
    val runnable = Runnable {
        run {
            if (handlerCount == timeCount) {
                handlerState = false
                lottie.setAnimation("req_step02.json")
                lottie.loop(true)
                lottie.playAnimation()
            }
        }
    }

    when (anim_status) {
        1 -> {
            handlerCount++
            lottie.cancelAnimation()
            lottie.visibility = View.VISIBLE
            lottie.setAnimation("req_step01.json")
            lottie.loop(false)
            lottie.playAnimation()
            handler.postDelayed(runnableState, 1900)
            handler.postDelayed(runnable, 2000)
        }
        2 -> {
            handlerState = false
            lottie.cancelAnimation()
            lottie.visibility = View.VISIBLE
            lottie.setAnimation("req_step03.json")
            lottie.loop(false)
            lottie.playAnimation()
        }
        3 -> {
            handlerState = false
            lottie.cancelAnimation()
            lottie.visibility = View.GONE
        }
        else -> {
            lottie.cancelAnimation()
            lottie.visibility = View.GONE
        }
    }
}

@BindingAdapter("setModifyButtonVisibility", "setModifyButtonVisibility2")
fun setModifyButtonVisibility(imageButton: ImageButton, id: Long, status: Int) {
    if (status == 0 && id == PromissuApplication.diskCache!!.userId) {
        imageButton.visibility = View.VISIBLE
    } else {
        imageButton.visibility = View.GONE
    }
}

@BindingAdapter("setPromisePointButtonVisibility")
fun setPromisePointButtonVisibility(imageButton: ImageButton, isSocketOpen: Boolean) {
    if (isSocketOpen) {
        imageButton.visibility = View.VISIBLE
    } else {
        imageButton.visibility = View.GONE
    }
}

@BindingAdapter("setPromisePointTextVisibility")
fun setPromisePointTextVisibility(text: TextView, isSocketOpen: Boolean) {
    if (isSocketOpen) {
        text.visibility = View.VISIBLE
    } else {
        text.visibility = View.GONE
    }
}

@BindingAdapter("setPromisePointText")
fun setPromisePointText(text: TextView, point: Int?) {
    if (point != null) {
        text.text = "" + point
    }
}

@BindingAdapter("setAttendanceRvItems")
fun setAttendanceRvItems(recyclerView: RecyclerView, itemList: List<Participant.Response>?) {
    if (itemList != null) {
        (recyclerView.adapter as DetailAttendanceAdapter).setData(itemList)
    } else {
        // TODO 데이터 정보 없음 처리.
    }
}