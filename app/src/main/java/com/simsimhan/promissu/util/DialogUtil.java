package com.simsimhan.promissu.util;

import android.content.Context;
import com.afollestad.materialdialogs.MaterialDialog;
import com.simsimhan.promissu.R;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class DialogUtil {


    public static void showSimpleDialog(Context context,
                                        @StringRes int title,
                                        @StringRes int positiveText,
                                        @StringRes int negativeText,
                                        CharSequence body,
                                        MaterialDialog.SingleButtonCallback onPositiveCallback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(body)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .positiveColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .onPositive(onPositiveCallback)
                .show();
    }
}
