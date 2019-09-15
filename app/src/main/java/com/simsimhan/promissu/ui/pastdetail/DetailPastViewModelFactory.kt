package com.simsimhan.promissu.ui.pastdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simsimhan.promissu.network.model.PromiseResponse

class DetailPastViewModelFactory(val promise: PromiseResponse) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(PromiseResponse::class.java).newInstance(promise)
    }
}