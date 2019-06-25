package com.simsimhan.promissu.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakao.usermgmt.response.MeV2Response
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.Login
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.net.ConnectException

class LoginViewModel : BaseViewModel() {

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    private val _onSuccess = MutableLiveData<Boolean>()
    val onSuccess: LiveData<Boolean>
        get() = _onSuccess

    init {
        _onSuccess.value = false
    }

    fun login(userSessionToken: String, result: MeV2Response) {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .loginKakao(PromissuApplication.getVersionInfo(), Login.Request(userSessionToken))
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
                    try {
                        when ((onError as HttpException).code()) {
                            420 -> {
                                Timber.e("require update: %s", onError.toString())
                                _toastMsg.postValue("최신 버전의 업데이트가 필요합니다.")
                            }
                            500 -> {
                                Timber.e("Server Error(): %s", onError.toString())
                                _toastMsg.postValue("서버 점검중입니다. 잠시후 다시 시도해주세요.")
                            }
                            else -> {
                                Timber.e("onSessionClosed(): %s", onError.toString())
                                _toastMsg.postValue("네트워크 상태를 확인 후, 로그인을 재시도 해주세요.")
                            }
                        }
                    }catch (e:Throwable){
                        _toastMsg.postValue("서버 접속이 원활하지 않습니다. 잠시후 다시 시도해주세요.")
                    }
                }))
    }

}