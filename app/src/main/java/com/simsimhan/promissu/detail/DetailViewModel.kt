package com.simsimhan.promissu.detail

import android.view.View
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
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.SingleLiveEvent
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*

class DetailViewModel(val promise: Promise.Response) : BaseViewModel(), DetailEventListener {

    private val _response = MutableLiveData<Promise.Response>() // 전체 데이터 리스트
    val reponse: LiveData<Promise.Response>
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


    init {
        _trackingMode.postValue(1)
        _response.postValue(promise)

        val meetingLatLng = LatLng(promise.location_lat.toDouble(), promise.location_lon.toDouble())
        _meetingLocation.postValue(meetingLatLng)

//        addDisposable(PromissuApplication.getRetrofit()
//                .create(AuthAPI::class.java)
//                .getParticipants("Bearer " + PromissuApplication.getDiskCache().userToken, promise.id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        { onNext -> _participants.postValue(onNext) },
//                        { onError ->
//                            Timber.e(onError)
//                        }
//                ))

        val list = listOf(Participant.Response(1, "태성"),
                Participant.Response(2, "준영"),
                Participant.Response(2, "지미"),
                Participant.Response(2, "태준"),
                Participant.Response(2, "영니"),
                Participant.Response(0, "초대"))

        _participants.postValue(list)
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
                                ""+ promiseDate.getYear() + "년 "
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

}

interface DetailEventListener {
    fun onClickInviteButton(view: View)
}