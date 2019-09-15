package com.simsimhan.promissu.ui.pastList

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.model.AppointmentResponse
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.repository.PastListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class PastListViewModel(
        private val api : PastListRepository
) : BaseViewModel(), PastListEventListener{

    private val myToken = PromissuApplication.diskCache?.userToken

    private val _pastList = MutableLiveData<List<PromiseResponse>>()
    val pastList : LiveData<List<PromiseResponse>>
        get() = _pastList

    private val _onClickPastListItem = MutableLiveData<PromiseResponse>()
    val onClickPastListItem : LiveData<PromiseResponse>
        get() = _onClickPastListItem

    val mainTextOnLayout = ObservableField<String>()
    val subTextOnLayout = ObservableField<String>()

    init {
        fetchInvitingList()
        getAttendanceCount()
    }

    private fun fetchInvitingList(){
        addDisposable(api.getPastList(PromissuApplication.getVersionInfo(),"Bearer $myToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _pastList.postValue(it.data)
                    mainTextOnLayout.set("${it.total}개의 지난약속 중..")
                }, {
                    Timber.e(it)
                }))
    }

    private fun getAttendanceCount(){
        addDisposable(api.getAttendanceResult(PromissuApplication.getVersionInfo(),"Bearer $myToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    subTextOnLayout.set("출석 ${it.attendance} / 지각 ${it.latecomer} / 결석 ${it.absentee}")
                }, {
                    Timber.e(it)
                }))
    }

    override fun onClickPastItem(promise: PromiseResponse) {
        _onClickPastListItem.postValue(promise)
    }
}

interface PastListEventListener{
    fun onClickPastItem(promise: PromiseResponse)
}