package com.simsimhan.promissu.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun isNetworkConnected(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = manager.activeNetworkInfo
    return activeNetwork != null
}