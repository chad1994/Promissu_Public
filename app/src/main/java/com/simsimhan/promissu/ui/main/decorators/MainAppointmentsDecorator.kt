package com.simsimhan.promissu.ui.main.decorators

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.util.ScreenUtil
import timber.log.Timber

class MainAppointmentsDecorator(
        context: Context
) : RecyclerView.ItemDecoration() {

    private val size4 = ScreenUtil.convertDpToPixel( 4f,context)
    private val size10 = ScreenUtil.convertDpToPixel( 10f,context)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val lastChild = parent.adapter?.itemCount?.minus(1)

        if(position != lastChild){
            outRect.bottom = size4.toInt()
        }
    }


}