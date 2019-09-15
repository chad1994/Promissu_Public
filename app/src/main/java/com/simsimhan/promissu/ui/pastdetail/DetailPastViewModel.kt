package com.simsimhan.promissu.ui.pastdetail

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.util.SingleLiveEvent
import com.simsimhan.promissu.util.StringUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DetailPastViewModel(val promise: PromiseResponse) : BaseViewModel() {

    private val _response = MutableLiveData<PromiseResponse>() // 전체 데이터 리스트
    val response: LiveData<PromiseResponse>
        get() = _response

    private val _participants = MutableLiveData<List<Participant.Response>>() //참여자 랭킹
    val participants: LiveData<List<Participant.Response>>
        get() = _participants

    private val _openLocationMap = SingleLiveEvent<Any>()
    val openLocationMap: LiveData<Any>
        get() = _openLocationMap

    val title = ObservableField<String>()
    val startDate = ObservableField<String>()
    val locationName = ObservableField<String>()
    val locationDetail = ObservableField<String>()
    val participantNum = ObservableField<String>()

    init {
        _response.value = promise
        initRoomInfo()
        fetchRanking()
    }

    private fun initRoomInfo() {
        title.set(_response.value!!.promise.title)
        startDate.set("" + (_response.value!!.promise.datetime.month + 1) + "월 " + _response.value!!.promise.datetime.date + "일 " + _response.value!!.promise.datetime.hours + "시 " + StringUtil.addPaddingIfSingleDigit(_response.value!!.promise.datetime.minutes) + "분")
        locationName.set(_response.value!!.promise.location_name)
        locationDetail.set(_response.value!!.promise.location)
    }

    private fun fetchRanking() {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .getParticipants(PromissuApplication.getVersionInfo(), "Bearer " + PromissuApplication.diskCache!!.userToken, promise.promise.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onNext ->
                            participantNum.set((StringUtil.addPaddingIfSingleDigit(onNext.size)).toString() + " 명")
                            val sortedList = onNext.sortedWith(comparator = Participant.CompareByStatus())
                            _participants.postValue(sortedList)
                        },
                        { onError ->
                            Timber.e(onError)
                        }
                ))
    }

    fun onClickLocationOnMap(){
        _openLocationMap.call()
    }
}