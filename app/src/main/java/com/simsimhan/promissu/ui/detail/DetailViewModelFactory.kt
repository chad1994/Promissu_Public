package com.simsimhan.promissu.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simsimhan.promissu.network.model.Promise

class DetailViewModelFactory(val promise: Promise.Response) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Promise.Response::class.java).newInstance(promise)
    }
}