package com.simsimhan.promissu.ui.pending

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.repository.PendingRepository
import com.simsimhan.promissu.util.SingleLiveEvent
import com.simsimhan.promissu.util.StringUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import timber.log.Timber
import java.util.*

class PendingViewModel(
        private val api: PendingRepository
) : BaseViewModel(), PendingEventListener{

    private val myToken = PromissuApplication.diskCache?.userToken

    private val _promise = MutableLiveData<Promise.Response>()
    val promise: LiveData<Promise.Response>
        get() = _promise

    private val _participants = MutableLiveData<List<Participant.Response>>()
    val participants: LiveData<List<Participant.Response>>
        get() = _participants

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    private val _openLocationMap = SingleLiveEvent<Any>()
    val openLocationMap: LiveData<Any>
        get() = _openLocationMap


    val dateTimeOnLayout = ObservableField<String>()
    val participantsSizeOnLayout = ObservableField<String>()
    val lateTimeGuideOnLayout = ObservableField<String>()
    val remainingDateOnLayout = ObservableField<String>()

    fun fetchPromiseData(promise: Promise.Response) {
        _promise.postValue(promise)
        setDateTimeOnLayout(promise)
        setlateTimeGuideOnLayout(promise.late_range)
        setRemainingDateOnLayout(promise.datetime)
    }

    fun fetchParticipants(roomId: Int) {
        addDisposable(api.getParticipants(PromissuApplication.getVersionInfo(), "Bearer $myToken", roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _participants.postValue(it)
                    setParticipantsSizeOnLayout(it.size)
                }, {
                    Timber.e(it)
                    _toastMsg.postValue("참여자 정보를 불러오는 중 문제가 발생했습니다.")
                }))
    }

    private fun setDateTimeOnLayout(promise: Promise.Response) {
        dateTimeOnLayout.set(
                "" + (promise.datetime.month + 1) + "월 "
                        + promise.datetime.date + "일 "
                        + promise.datetime.hours + "시 "
                        + StringUtil.addPaddingIfSingleDigit(promise.datetime.minutes) + "분")
    }

    private fun setlateTimeGuideOnLayout(lateRange: Int) {
        lateTimeGuideOnLayout.set(String.format(PromissuApplication.instance?.getString(R.string.inviting_late_time_guide)!!, lateRange)) //
    }

    private fun setParticipantsSizeOnLayout(size: Int) {
        participantsSizeOnLayout.set("$size 명")
    }

    private fun setRemainingDateOnLayout(dateTime : Date){
        val now = DateTime()
        val promiseStartDate = DateTime(dateTime)
        val dayDifference = Days.daysBetween(now, promiseStartDate)
        if(dayDifference.days>0) {
            remainingDateOnLayout.set("${dayDifference.days}일 남았습니다!")
        }else{
            val hourDifference = Hours.hoursBetween(now, promiseStartDate)
            remainingDateOnLayout.set("${hourDifference.hours}시간 남았습니다!")
        }
    }

    override fun onClickLocationMap() {
        _openLocationMap.call()
    }

}

interface PendingEventListener{
    fun onClickLocationMap()
}