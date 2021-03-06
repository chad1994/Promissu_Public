//package com.simsimhan.promissu.ui.promise
//
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import com.simsimhan.promissu.BaseViewModel
//import com.simsimhan.promissu.PromissuApplication
//import com.simsimhan.promissu.network.AuthAPI
//import com.simsimhan.promissu.network.model.PromiseResponse
//import com.simsimhan.promissu.network.model.Promise
//import com.simsimhan.promissu.util.NavigationUtil
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import timber.log.Timber
//
//class PromiseViewModel : BaseViewModel(), PromiseItemEventListener {
//
//    val token: String = PromissuApplication.diskCache!!.userToken
//
//    private val _onListLoadedPast = MutableLiveData<List<PromiseResponse>>()
//    val onListLoadedPast: LiveData<List<PromiseResponse>>
//        get() = _onListLoadedPast
//
//    private val _onListLoadedRecent = MutableLiveData<List<PromiseResponse>>()
//    val onListLoadedRecent: LiveData<List<PromiseResponse>>
//        get() = _onListLoadedRecent
//
//    private val _errToastMsg = MutableLiveData<String>()
//    val errToastMsg: LiveData<String>
//        get() = _errToastMsg
//
//    private val _toastMsg = MutableLiveData<String>()
//    val toastMsg: LiveData<String>
//        get() = _toastMsg
//
//    //    private val _enterRoom = MutableLiveData<Promise.Response>()
////    val enterRoom: LiveData<Promise.Response>
////        get() = _enterRoom
////
//    private val _deleteRoom = MutableLiveData<Promise.Response>()
//    val deleteRoom: LiveData<Promise.Response>
//        get() = _deleteRoom
//
//
//    fun fetch(isPastPromise: Boolean) {
//        addDisposable(PromissuApplication.retrofit!!
//                .create(AuthAPI::class.java)
//                .getMyPromise(PromissuApplication.getVersionInfo(), "Bearer $token", 0, 99, if (isPastPromise) "past" else "future")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ onNext ->
//                    if (isPastPromise) {
//                        _onListLoadedPast.postValue(onNext)
//                    } else {
//                        _onListLoadedRecent.postValue(onNext)
//                    }
//                }, { onError ->
//                    Timber.e(onError)
//                    PromissuApplication.diskCache!!.clearUserData()
//                    _errToastMsg.postValue("세션만료로 로그아웃 되었습니다. 다시 로그인 해주세요.")
//                }))
//    }
//
//    override fun itemClickListener(view: View, response: PromiseResponse, isPast: Boolean) {
//        NavigationUtil.enterRoom(view.context as AppCompatActivity, response, isPast)
//    }
//
//    override fun itemLongCLickListener(response: Promise.Response): Boolean {
//        if (response.status == 0) {
//            _deleteRoom.postValue(response)
//        } else if (response.status == 1) {
//            _toastMsg.postValue("대기중인 모임은 나갈 수 없습니다.")
//        } else if (response.status == 2) {
//            _toastMsg.postValue("지난 모임은 나갈 수 없습니다.")
//        }
//        return true
//    }
//}
//
//interface PromiseItemEventListener {
//    fun itemClickListener(view: View, response: PromiseResponse, isPast: Boolean)
//    fun itemLongCLickListener(response: Promise.Response): Boolean
//}