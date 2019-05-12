package com.simsimhan.promissu.promise

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.NavigationUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PromiseViewModel : BaseViewModel(), PromiseItemEventListener {

    private val token: String

    private val _onListLoadedPast = MutableLiveData<List<Promise.Response>>()
    val onListLoadedPast: LiveData<List<Promise.Response>>
        get() = _onListLoadedPast

    private val _onListLoadedRecent = MutableLiveData<List<Promise.Response>>()
    val onListLoadedRecent: LiveData<List<Promise.Response>>
        get() = _onListLoadedRecent

    private val _errToastMsg = MutableLiveData<String>()
    val errToastMsg: LiveData<String>
        get() = _errToastMsg

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    //    private val _enterRoom = MutableLiveData<Promise.Response>()
//    val enterRoom: LiveData<Promise.Response>
//        get() = _enterRoom
//
    private val _deleteRoom = MutableLiveData<Promise.Response>()
    val deleteRoom: LiveData<Promise.Response>
        get() = _deleteRoom


    init {
        token = PromissuApplication.diskCache!!.userToken
    }


    fun fetch(isPastPromise: Boolean) {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .getMyPromise("Bearer $token", 0, 9, if (isPastPromise) "past" else "future")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ onNext ->
                    if (isPastPromise) {
                        _onListLoadedPast.postValue(onNext)
                    } else {
                        _onListLoadedRecent.postValue(onNext)
                    }
                }, { onError ->
                    Timber.e(onError)
                    PromissuApplication.diskCache!!.clearUserData()
                    _errToastMsg.postValue("서버 점검중입니다. 잠시후 다시 시도해주세요.")
                }))
    }

    override fun itemClickListener(view: View, response: Promise.Response) {
        NavigationUtil.enterRoom(view.context as AppCompatActivity, response)
    }

    override fun itemLongCLickListener(response: Promise.Response): Boolean {
        if (response.status == 0) {
            _deleteRoom.postValue(response)
        } else if (response.status == 1) {
            _toastMsg.postValue("대기중인 모임은 나갈 수 없습니다.")
        } else if (response.status == 2) {
            _toastMsg.postValue("지난 모임은 나갈 수 없습니다.")
        }
        return true
    }
}

interface PromiseItemEventListener {
    fun itemClickListener(view: View, response: Promise.Response)
    fun itemLongCLickListener(response: Promise.Response): Boolean
}