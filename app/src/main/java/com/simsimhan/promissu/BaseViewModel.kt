package com.simsimhan.promissu

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    fun addDisposable(d: Disposable) {
        compositeDisposable.add(d)
    }

    fun getCompositeDisposable() = compositeDisposable

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}