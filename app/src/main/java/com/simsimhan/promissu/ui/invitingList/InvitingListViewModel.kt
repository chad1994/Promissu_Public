package com.simsimhan.promissu.ui.invitingList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.model.AttendanceResult
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.repository.InvitingListRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class InvitingListViewModel(
        val api : InvitingListRepository
) : BaseViewModel() , InvitingListListener{

    private val myToken = PromissuApplication.diskCache?.userToken

    private val _invitingList = MutableLiveData<List<PromiseResponse>>()
    val invitingList : LiveData<List<PromiseResponse>>
        get() = _invitingList

    private val _onClickInvitingListItem = MutableLiveData<Promise.Response>()
    val onClickInvitingListItem : LiveData<Promise.Response>
        get() = _onClickInvitingListItem

    private val _onClickInvitingListItemThrottle = PublishSubject.create<Promise.Response>()

    init {
        fetchInvitingList()

        addDisposable(_onClickInvitingListItemThrottle.throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe { _onClickInvitingListItem.value = it })

    }

    fun fetchInvitingList(){
        addDisposable(api.getInvitingList(PromissuApplication.getVersionInfo(),"Bearer $myToken")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _invitingList.postValue(it.data)
                }, {
                    Timber.e(it)
                }))
    }

    override fun onClickListItem(promise: Promise.Response) {
        _onClickInvitingListItemThrottle.onNext(promise)
    }

}

interface InvitingListListener {
    fun onClickListItem(promise: Promise.Response)
}
