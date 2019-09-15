package com.simsimhan.promissu.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.AuthAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun showSimpleDialog(context: Context,
                     @StringRes title: Int,
                     @StringRes positiveText: Int,
                     @StringRes negativeText: Int,
                     body: CharSequence,
                     onPositiveCallback: (Any, Any) -> Unit) {
    MaterialDialog.Builder(context)
            .title(title)
            .content(body)
            .positiveText(positiveText)
            .negativeText(negativeText)
            .positiveColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .onPositive(onPositiveCallback)
            .show()
}

fun buildDialog(
        context: Context,
        text1 : CharSequence,
        text2 : CharSequence,
        cancelButtonText : CharSequence,
        acceptButtonText : CharSequence,
        onAcceptCall : () -> Unit
        ) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_request)
    val textview1 = dialog.findViewById<TextView>(R.id.dialog_text1)
    val textview2 = dialog.findViewById<TextView>(R.id.dialog_text2)
    val btnCancel = dialog.findViewById<Button>(R.id.dialog_button_cancel)
    val btnAccept = dialog.findViewById<Button>(R.id.dialog_button_accept)
    textview1.text = text1
    textview2.text = text2
    btnCancel.text = cancelButtonText
    btnAccept.text = acceptButtonText
    btnAccept.setOnClickListener {
       onAcceptCall()
    }
    btnCancel.setOnClickListener { dialog.dismiss() }
    dialog.setCancelable(false)
    dialog.show()
}

