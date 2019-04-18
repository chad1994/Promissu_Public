package com.simsimhan.promissu.detail

import android.os.CountDownTimer
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import com.naver.maps.map.overlay.Marker
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.LocationEvent
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import org.joda.time.DateTime
import org.joda.time.Hours
import org.joda.time.Minutes
import org.joda.time.Seconds
import org.json.JSONObject
import timber.log.Timber
import java.util.*

class DetailViewModel(val promise: Promise.Response) : BaseViewModel(), DetailEventListener {


    private val _response = MutableLiveData<Promise.Response>() // 전체 데이터 리스트
    val response: LiveData<Promise.Response>
        get() = _response

    private val _naverMap = MutableLiveData<NaverMap>()
    val naverMap: LiveData<NaverMap>
        get() = _naverMap

    private val _isArrive = MutableLiveData<Boolean>()
    val isArrive: LiveData<Boolean>
        get() = _isArrive

    private val _userMarkers = MutableLiveData<List<Marker>>()
    val userMarkers: LiveData<List<Marker>>
        get() = _userMarkers

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

    private val _isSocketOpen = MutableLiveData<Boolean>()
    val isSocketOpen: LiveData<Boolean>
        get() = _isSocketOpen

    private val socket by lazy { IO.socket(BuildConfig.SOCKET_URL) }

    private val _locationEvents = MutableLiveData<HashMap<Int, LocationEvent>>()
    val locationEvents: LiveData<HashMap<Int, LocationEvent>>
        get() = _locationEvents

    private val _dialogResponse = SingleLiveEvent<Any>()
    val dialogResponse: LiveData<Any>
        get() = _dialogResponse

    private val _sendLocationRequest = MutableLiveData<Participant.Request>()
    val sendLocationRequest: LiveData<Participant.Request>
        get() = _sendLocationRequest

    private val _attendMyMarker = MutableLiveData<Boolean>()
    val attendMyMarker: LiveData<Boolean>
        get() = _attendMyMarker

    private val _timerString =  MutableLiveData<String>()
    val timerString: LiveData<String>
        get() = _timerString

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    val title = ObservableField<String>()
    val startDate = ObservableField<String>()
    val locationName = ObservableField<String>()
    val participantNum = ObservableField<String>()
    val myParticipation = ObservableField<Int>()
    lateinit var countDownTimer : CountDownTimer

    init {
        _trackingMode.value = 1
        _response.value = promise
        _isSocketOpen.value = false
        _attendMyMarker.value = false
        _isArrive.value = false
        val meetingLatLng = LatLng(promise.location_lat.toDouble(), promise.location_lon.toDouble())
        _meetingLocation.postValue(meetingLatLng)
        initRoomInfo()
        fetchParticipants()
        setupTimer()
    }

    private fun initRoomInfo() {
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

    private fun fetchParticipants() {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .getParticipants("Bearer " + PromissuApplication.diskCache!!.userToken, promise.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onNext ->
                            participantNum.set((onNext.size).toString() + " 명")
                            onNext.forEach {
                                if (it.kakao_id == PromissuApplication.diskCache!!.userId.toInt()) {
                                    myParticipation.set(it.participation)
                                }
                            }
                            _participants.value = onNext
                        },
                        { onError ->
                            Timber.e(onError)
                        }
                ))

    }

    fun setSpreadState(bool: Boolean) {
        _isSpread.value = bool
    }

    fun setSocketReady(bool: Boolean) {
        _isSocketOpen.value = bool

        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("participation", myParticipation.get())
            addProperty("token", PromissuApplication.diskCache!!.userToken)
            Timber.d("@@@LOGLOG:" + promise.id + "/" + myParticipation.get() + "/" + PromissuApplication.diskCache!!.userToken)
        }
        val jsonReq = JSONObject(jsonObject.toString())

        socket.on("connect") {
            socket.emit("location.join", jsonReq)
            Timber.d("@@@Connect on")
        }
        socket.on("location.info") {
            val jsonParser = JsonParser()
            val data = jsonParser.parse("" + it[0]) as JsonArray
            val gson = Gson()
            val locationEvent = gson.fromJson(data, Array<LocationEvent>::class.java)
            val map = HashMap<Int, LocationEvent>()
            locationEvent.forEach { event ->
                map[event.partId] = event
            }
            _locationEvents.postValue(map)
            map.forEach { map ->
                Timber.d("@@@Data: " + map.toString())
            }
            //TODO: info 조건 세분화 : 요청에 대해 나에게 온 요청인지 확인 후 응답, 응답에 대해 다른 인원에 대한 위치 변경 내용 갱신, 응답에 대해 거절된 경우 인지에 따른 view 갱신
        }
        socket.on("location.error") {
            Timber.d("@@@LOCATION ERROR: %s", it[0].toString())
        }
        socket.connect()
    }

    fun notifyEventInfo() {
        checkIsMyData()
        updateUserMarkers()
    }

    fun checkArrive(bool: Boolean) {
        _isArrive.postValue(bool)
    }

    private fun checkIsMyData() {
        if (_locationEvents.value!!.keys.contains(myParticipation.get())) { //내 키를 가지고 있고
            if (_locationEvents.value!![myParticipation.get()]!!.status == 1) { //내 상태가 요청이 온 상태라면
                _dialogResponse.call()
            }
            if( _locationEvents.value!![myParticipation.get()]!!.status == 4){
                _attendMyMarker.postValue(true)
            }
        }
    }

    private fun updateUserMarkers() {
        val list = ArrayList<Marker>()
        _locationEvents.value!!
                .filterNot { it.value.partId == myParticipation.get() }
                .filterNot { it.value.status == 4 }
                .filterNot { it.value.lat == 0.0 && it.value.lon == 0.0 }
                .forEach {
                    val marker = Marker()
                    marker.position = LatLng(it.value.lat, it.value.lon)
                    marker.tag = it.value.nickname
                    Timber.d("@@@Update Marker: " + it.value.partId + "/")
                    list.add(marker)
                }
        _userMarkers.postValue(list)
    }

    fun sendLocationRequest(partId: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("target", partId)
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.request", jsonReq)
    }

    fun sendLocationResponse(lon: Double, lat: Double) {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("participation", myParticipation.get())
            addProperty("lon", lon)
            addProperty("lat", lat)
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.response", jsonReq)
    }

    fun sendLocationReject() {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("participation", myParticipation.get())
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.reject", jsonReq)
    }

    fun sendLocationAttend(lon:Double,lat:Double) {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("participation", myParticipation.get())
            addProperty("lon",lon)
            addProperty("lat",lat)
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.attend", jsonReq)
    }

    private fun setupTimer(){
        val now = DateTime()
        val start = DateTime(promise.start_datetime)
        val betweenSeconds = Seconds.secondsBetween(now,start)
        var remainSeconds = betweenSeconds.seconds
        countDownTimer = object : CountDownTimer(3600000,1000){
            override fun onFinish() {
            }
            override fun onTick(millisUntilFinished: Long) {
                remainSeconds -= 1
                if(remainSeconds%60 < 10){
                    _timerString.postValue("약속 시작까지 "+remainSeconds/60+"분 0"+remainSeconds%60+"초 남았습니다")
                }else {
                    _timerString.postValue("약속 시작까지 " + remainSeconds / 60 + "분 " + remainSeconds % 60 + "초 남았습니다")
                }
            }
        }
    }

    fun startTimer(){
        countDownTimer.start()
    }

    fun removeTimer(){
        countDownTimer.cancel()
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

    override fun onClickRequestLocation(partId: Int, nickname: String) {
        if (isSocketOpen.value!! && partId != myParticipation.get())
            if (_locationEvents.value!![partId]!!.point <= 0)
                _toastMsg.postValue("해당 사용자에게 더 이상 위치를 요청할 수 없습니다.")
            else
                _sendLocationRequest.postValue(Participant.Request(partId, nickname))
    }

}

interface DetailEventListener {
    fun onClickInviteButton(view: View)
    fun onClickRequestLocation(partId: Int, nickname: String)
}