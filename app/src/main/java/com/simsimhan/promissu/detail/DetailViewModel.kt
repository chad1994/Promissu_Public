package com.simsimhan.promissu.detail

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.LinkObject
import com.kakao.message.template.LocationTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*

class DetailViewModel(val promise: Promise.Response) : BaseViewModel(), DetailEventListener {

    private val _response = MutableLiveData<Promise.Response>() // 전체 데이터 리스트
    val response: LiveData<Promise.Response>
        get() = _response

    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: LiveData<NaverMap>
        get() = _naverMap

    private val _trackingMode = MutableLiveData<Int>()
    val trackingMode: LiveData<Int> // 1:nothing ,2: tracking ,
        get() = _trackingMode

    private val _onBackPressed = SingleLiveEvent<Any>()
    val onBackPressed: LiveData<Any>
        get() = _onBackPressed

    private val _meetingLocation = MutableLiveData<LatLng>()
    val meetingLocation: LiveData<LatLng>
        get() = _meetingLocation

    private val _participants = MutableLiveData<List<Participant.Response>>()
    val participants: LiveData<List<Participant.Response>>
        get() = _participants

    private val _isSpread = MutableLiveData<Boolean>()
    val isSpread: LiveData<Boolean>
        get() = _isSpread

    val title = ObservableField<String>()
    val startDate = ObservableField<String>()
    val locationName = ObservableField<String>()
    val participantNum = ObservableField<String>()

    init {
        _trackingMode.postValue(1)
        _response.postValue(promise)
        val meetingLatLng = LatLng(promise.location_lat.toDouble(), promise.location_lon.toDouble())
        _meetingLocation.postValue(meetingLatLng)
        initRoomInfo()
        fetchParticipants()
    }

    private fun initRoomInfo(){
        title.set(promise.title)
        startDate.set("" + (promise.start_datetime.month + 1) + "월 " + promise.start_datetime.date + "일 " + promise.start_datetime.hours + "시 " + promise.start_datetime.minutes + "분")
        locationName.set((promise.location_name))
    }

    fun onClickedCurrentLocation() {
        Timber.d("onClick: currentLocationButton")
        when {
            trackingMode.value == 1 -> _trackingMode.postValue(2)
            trackingMode.value == 2 -> _trackingMode.postValue(3)
            trackingMode.value == 3 -> _trackingMode.postValue(1)
        }
    }

    fun onClickedBackButton() {
        _onBackPressed.call()
    }

    fun setNaverMap(naverMap: NaverMap) {
        _naverMap.postValue(naverMap)
    }

    fun setMyMarker() {

    }

    private fun fetchParticipants(){
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .getParticipants("Bearer " + PromissuApplication.diskCache!!.userToken, promise.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onNext ->
                            _participants.value = onNext
                            participantNum.set((onNext.size).toString() + " 명")
                        },
                        { onError ->
                            Timber.e(onError)
                        }
                ))

    }

    override fun onClickInviteButton(view: View) {
        Timber.d("@@@@invite clicked")
        val promiseDate = DateTime(promise.start_datetime)

        val params = LocationTemplate.newBuilder(promise.location + " 좌표: (" + promise.location_lat + ", " + promise.location_lon + ")",
                ContentObject.newBuilder(promise.title,
                        "https://i.pinimg.com/originals/92/e4/43/92e443862a7ae5db7cf74b41db2f5e37.jpg",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(
                                "" + promiseDate.getYear() + "년 "
                                        + promiseDate.getMonthOfYear() + "월 "
                                        + promiseDate.getDayOfMonth() + "일 "
                                        + promise.description)
                        .build())
                .setAddressTitle(promise.location + " 좌표: (" + promise.location_lat + ", " + promise.location_lon + ")")
                .addButton(ButtonObject("앱에서 보기", LinkObject.newBuilder()
                        .setWebUrl("'https://developers.kakao.com")
                        .setMobileWebUrl("'https://developers.kakao.com")
                        .setAndroidExecutionParams("roomID=" + promise.id)
                        .setIosExecutionParams("roomID=" + promise.id)
                        .build()))
                .build()

        val serverCallbackArgs = HashMap<String, String>()
        serverCallbackArgs["user_id"] = "\${current_user_id}"
        serverCallbackArgs["product_id"] = "\${shared_product_id}"

        KakaoLinkService.getInstance().sendDefault(view.context, params, serverCallbackArgs, object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult) {
                Timber.e(errorResult.exception)
            }

            override fun onSuccess(result: KakaoLinkResponse) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                Timber.d("onSuccess(): $result")
            }
        })

    }

    fun setSpreadState(bool:Boolean){
        _isSpread.value = bool
    }

}

interface DetailEventListener {
    fun onClickInviteButton(view: View)
}