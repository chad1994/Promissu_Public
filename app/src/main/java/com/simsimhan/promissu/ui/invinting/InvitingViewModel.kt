package com.simsimhan.promissu.ui.invinting

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.repository.InvitingRepository
import com.simsimhan.promissu.util.SingleLiveEvent
import com.simsimhan.promissu.util.StringUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import timber.log.Timber

class InvitingViewModel(
        private val api: InvitingRepository
) : BaseViewModel(), InvitingEventListener {

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

    private val _confirmPromise = SingleLiveEvent<Any>()
    val confirmPromise: LiveData<Any>
        get() = _confirmPromise

    private val _onClickLeftAppointment = SingleLiveEvent<Any>()
    val onClickLeftAppointment: LiveData<Any>
        get() = _onClickLeftAppointment

    private val _onClickDeleteAppointment = SingleLiveEvent<Any>()
    val onClickDeleteAppointment: LiveData<Any>
        get() = _onClickDeleteAppointment

    private val _finishActivity = SingleLiveEvent<Any>()
    val finishActivity : LiveData<Any>
        get() = _finishActivity

    private val _openLocationMap = SingleLiveEvent<Any>()
    val openLocationMap: LiveData<Any>
        get() = _openLocationMap

    val dateTimeOnLayout = ObservableField<String>()
    val participantsSizeOnLayout = ObservableField<String>()
    val lateTimeGuideOnLayout = ObservableField<String>()

    init {

    }

    fun fetchPromiseData(promise: Promise.Response) {
        _promise.postValue(promise)
        setDateTimeOnLayout(promise)
        setlateTimeGuideOnLayout(promise.late_range)
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

    private fun setParticipantsSizeOnLayout(size: Int) {
        participantsSizeOnLayout.set("$size 명")
    }

    fun leftAppointment(){
        addDisposable(api.leftAppointment(PromissuApplication.getVersionInfo(), "Bearer $myToken", _promise.value?.id
                ?: 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        _finishActivity.call()
                }, {
                    Timber.e(it)
                    _toastMsg.postValue("약속 나가기 도중 오류가 발생했습니다.")
                }))
    }

    fun deleteAppointment(){
        addDisposable(api.deleteAppointment(PromissuApplication.getVersionInfo(), "Bearer $myToken", _promise.value?.id
                ?: 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _finishActivity.call()
                }, {
                    Timber.e(it)
                    _toastMsg.postValue("약속 삭제 도중 오류가 발생했습니다.")
                }))
    }

    override fun onClickConfirmButton() {
        addDisposable(api.confirmAppointment(PromissuApplication.getVersionInfo(), "Bearer $myToken", _promise.value?.id
                ?: 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) {
                        _confirmPromise.call()
                    } else {
                        _toastMsg.postValue("확인중 오류가 발생했습니다.")
                    }
                }, {
                    Timber.e(it)
                    _toastMsg.postValue("서버 에러가 발생했습니다.")
                }))
    }

    override fun onClickInvitingButton() {
        val promiseDate = DateTime(_promise.value?.datetime)
        val templateId = PromissuApplication.instance?.applicationContext?.getString(R.string.kakaolink_template_id)

        val templateArgs = HashMap<String?, String?>()
        templateArgs["title"] = _promise.value?.title
        templateArgs["address"] = _promise.value?.location_name
        templateArgs["date"] = "" + promiseDate.year + "년 " +
                promiseDate.monthOfYear + "월 " +
                promiseDate.dayOfMonth + "일 " +
                StringUtil.addPaddingIfSingleDigit(promiseDate.hourOfDay) + "시 " +
                StringUtil.addPaddingIfSingleDigit(promiseDate.minuteOfHour) + "분"
        templateArgs["roomid"] = "roomid=" + _promise.value?.id

        val serverCallbackArgs = HashMap<String, String>()
        serverCallbackArgs["user_id"] = "\${current_user_id}"
        serverCallbackArgs["product_id"] = "\${shared_product_id}"

        KakaoLinkService.getInstance().sendCustom(PromissuApplication.instance?.applicationContext, templateId, templateArgs, serverCallbackArgs
                , object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult?) {
                Timber.e(errorResult!!.exception)
            }

            override fun onSuccess(result: KakaoLinkResponse?) {
                Timber.d("onSuccess(): $result")
            }
        })
    }

    override fun onClickLeftButton() {
        if(_promise.value?.admin_id==PromissuApplication.diskCache?.userId){
            _onClickDeleteAppointment.call()
        }else{
            _onClickLeftAppointment.call()
        }
    }

    override fun onClickLocationOnMap() {
        _openLocationMap.call()
    }
}

interface InvitingEventListener {
    fun onClickConfirmButton()
    fun onClickInvitingButton()
    fun onClickLeftButton()
    fun onClickLocationOnMap()
}