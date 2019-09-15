package com.simsimhan.promissu.ui.main

import android.os.CountDownTimer
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.FcmToken
import com.simsimhan.promissu.network.model.Participation
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.repository.MainRepository
import com.simsimhan.promissu.util.SingleLiveEvent
import com.simsimhan.promissu.util.StringUtil
import com.simsimhan.promissu.util.getDayofWeekForKorean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Seconds
import retrofit2.HttpException
import timber.log.Timber
import java.util.*

class MainViewModel(
        private val api: MainRepository
) : BaseViewModel(), MainEventListener {

    private val myToken = PromissuApplication.diskCache?.userToken
    private val currentDate = Calendar.getInstance()

    private val _todayAppointments = MutableLiveData<List<PromiseResponse>>()
    val todayAppointments: LiveData<List<PromiseResponse>>
        get() = _todayAppointments

    private val _pendingAppointments = MutableLiveData<List<PromiseResponse>>()
    val pendingAppointments: LiveData<List<PromiseResponse>>
        get() = _pendingAppointments

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    private val _onClickCreatePromise = SingleLiveEvent<Any>()
    val onClickCreatePromise: LiveData<Any>
        get() = _onClickCreatePromise

    private val _onClickInvitingListButton = SingleLiveEvent<Any>()
    val onClickInvitingListButton: LiveData<Any>
        get() = _onClickInvitingListButton

    private val _invitingListCount = MutableLiveData(0)
    val invitingListCount: LiveData<Int>
        get() = _invitingListCount

    private val _firstItemTimer = MutableLiveData<String>("00:00:00")
    val firstItemTimer: LiveData<String>
        get() = _firstItemTimer

    private val _enterPromiseResponse = MutableLiveData<Promise.Response>()
    val enterPromiseResponse: LiveData<Promise.Response>
        get() = _enterPromiseResponse

    private val _openPendingActivity = MutableLiveData<Promise.Response>()
    val openPendingActivity: LiveData<Promise.Response>
        get() = _openPendingActivity

    private val _openDetailActivity = MutableLiveData<PromiseResponse>()
    val openDetailActivity: LiveData<PromiseResponse>
        get() = _openDetailActivity


    private lateinit var countDownTimer: CountDownTimer
    private var countDownTimerState = false


    val currentDateOnLayout = ObservableField<String>()
    val pendingAppointmentsSize = ObservableField<String>()

    init {
        setCurrentDate()
        fetchRemoteData()
    }

    private fun setCurrentDate() {
        currentDateOnLayout.set("${currentDate.get(Calendar.MONTH) + 1}.${currentDate.get(Calendar.DATE)} ${getDayofWeekForKorean(currentDate.get(Calendar.DAY_OF_WEEK))}")
    }

    fun sendFcmTokenToServer() {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .updateFcmToken(PromissuApplication.getVersionInfo(), "Bearer " + PromissuApplication.diskCache?.userToken, FcmToken(PromissuApplication.diskCache?.fcmToken!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Timber.d("Success:: Fcm Token registered ") },
                        { Timber.e("fail:: %s", it.toString()) }
                ))
    }

    fun fetchRemoteData() {
        fetchTodayList()
        fetchPendingList()
        getInvitingListCount()
    }

    private fun fetchTodayList() {
        addDisposable(api.getImminentsPromise(PromissuApplication.getVersionInfo(), "Bearer $myToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _todayAppointments.postValue(it.data)
                    if (it.total != 0) {
                        if (countDownTimerState) {
                            countDownTimer.cancel()
                        }
                        setupFirstItemTimer(it.data[0].promise.datetime)
                        countDownTimer.start()
                        countDownTimerState = true
                    } else {
                        val emptyItem = arrayListOf<PromiseResponse>()
                        emptyItem.add(PromiseResponse(Promise.Response(0, "0", "0", "0", Date(), "0", "0", 0.0, 0.0, 0, 0, 0, Date(), Date()), Participation(0, 0)))
                        _todayAppointments.postValue(emptyItem)
                    }
                }, {
                    Timber.e(it)
                    _toastMsg.postValue("정보를 불러오는 중 문제가 발생했습니다.")
                })
        )
    }

    private fun fetchPendingList() {
        addDisposable(api.getPendingPromise(PromissuApplication.getVersionInfo(), "Bearer $myToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _pendingAppointments.postValue(it.data)
                    setPendingAppointmentsSize(it.total)
                }, {
                    Timber.e(it.message)
                    _toastMsg.postValue("정보를 불러오는 중 문제가 발생했습니다.")
                })
        )
    }

    private fun getInvitingListCount() {
        addDisposable(api.getInvitingListCount(PromissuApplication.getVersionInfo(), "Bearer $myToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _invitingListCount.postValue(it)
                }, {
                    Timber.e(it)
                })
        )
    }

    fun enterAppointment(roomId: String) {
        addDisposable(api.enterAppointment(PromissuApplication.getVersionInfo(), "Bearer $myToken", roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _enterPromiseResponse.postValue(it)
                }, {

                    when ((it as HttpException).code()) {
                        403 -> _toastMsg.postValue("이미 시작했거나 끝난 약속입니다.")
                        // TODO : 이미 참여한 모임 , 겹치는 날짜 모임 분기 처리
                    }
                })
        )
    }

    private fun setupFirstItemTimer(dateTime: Date) {
        val now = DateTime()
        val start = DateTime(dateTime)
        val betweenSeconds = Seconds.secondsBetween(now, start)
        var remainSeconds = betweenSeconds.seconds

        countDownTimer = object : CountDownTimer(3600000, 1000) {

            override fun onFinish() {
                countDownTimerState = false
            }

            override fun onTick(millisUntilFinished: Long) {
                remainSeconds -= 1
                _firstItemTimer.postValue("${StringUtil.addPaddingIfSingleDigit(remainSeconds / 3600)}:${StringUtil.addPaddingIfSingleDigit((remainSeconds % 3600) / 60)}:${StringUtil.addPaddingIfSingleDigit(remainSeconds % 60)}")
                if (remainSeconds < 0) {
                    _firstItemTimer.postValue("약속 시작")
                }
            }
        }
    }

    private fun setPendingAppointmentsSize(size: Int) {
        pendingAppointmentsSize.set("$size")
    }

    fun onClickCreatePromiseButton() {
        _onClickCreatePromise.call()
    }

    fun onClickInvitingListButton() {
        _onClickInvitingListButton.call()
    }

    fun clearTimer() {
        if (countDownTimerState) {
            countDownTimer.cancel()
        }
    }

    override fun onClickTodayItem(promise: PromiseResponse) {
        _openDetailActivity.postValue(promise)
    }

    override fun onClickPendingItem(promise: Promise.Response) {
        _openPendingActivity.postValue(promise)
    }

    override fun onCleared() {
        super.onCleared()
        if (countDownTimerState) {
            countDownTimer.cancel()
        }
    }


}

interface MainEventListener {
    fun onClickTodayItem(promise: PromiseResponse)
    fun onClickPendingItem(promise: Promise.Response)
}