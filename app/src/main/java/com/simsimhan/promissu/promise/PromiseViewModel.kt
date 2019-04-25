package com.simsimhan.promissu.promise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Promise
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PromiseViewModel : BaseViewModel(){

    private val token : String

    private val _onListLoadedPast = MutableLiveData<List<Promise.Response>>()
    val onListLoadedPast : LiveData<List<Promise.Response>>
    get() = _onListLoadedPast

    private val _onListLoadedRecent = MutableLiveData<List<Promise.Response>>()
    val onListLoadedRecent : LiveData<List<Promise.Response>>
        get() = _onListLoadedRecent

    private val _errToastMsg = MutableLiveData<String>()
    val errToastMsg : LiveData<String>
        get() = _errToastMsg

    init {
        token = PromissuApplication.diskCache!!.userToken
    }


    fun fetch(isPastPromise:Boolean){
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .getMyPromise("Bearer $token", 0, 9, if (isPastPromise) "past" else "future")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onNext ->
                    if(isPastPromise){
                        _onListLoadedPast.postValue(onNext)
                    }else{
                        _onListLoadedRecent.postValue(onNext)
                    }
                }, { onError ->
                    Timber.e(onError)
                    PromissuApplication.diskCache!!.clearUserData()
                    _errToastMsg.postValue("서버 점검중입니다. 잠시후 다시 시도해주세요.")
                }))
    }

}

interface PromiseItemEventListener{
    fun itemClickListener()
}