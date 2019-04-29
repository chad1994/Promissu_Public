package com.simsimhan.promissu.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakao.usermgmt.response.MeV2Response
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.Login
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LoginViewModel : BaseViewModel(){

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg : LiveData<String>
        get() = _toastMsg

    private val _onSuccess = MutableLiveData<Boolean>()
    val onSuccess : LiveData<Boolean>
        get() = _onSuccess

    init {
        _onSuccess.value = false
    }

    fun login(userSessionToken : String, result : MeV2Response){
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .loginKakao(Login.Request(userSessionToken))
                .doOnNext { next ->
                    PromissuApplication.diskCache!!.setUserData(result.nickname, result.id, result.thumbnailImagePath)
                    PromissuApplication.diskCache!!.userToken = next.token
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // save token
                    _onSuccess.postValue(true)
                }, { onError ->
                    Timber.e("onSessionClosed(): %s", onError.toString())
                    _toastMsg.postValue("서버 점검 중이거나, 인터넷 연결을 확인해주세요.")
                }))
    }

}