package com.simsimhan.promissu.ui.detail

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.LocationEvent
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.SingleLiveEvent
import com.simsimhan.promissu.util.StringUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.socket.client.IO
import org.joda.time.DateTime
import org.joda.time.Seconds
import org.json.JSONObject
import timber.log.Timber

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

    private val _attendedParticipants = MutableLiveData<List<Participant.Response>>()
    val attendedParticipants: LiveData<List<Participant.Response>>
        get() = _attendedParticipants

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

    private val _myLocationEvent = MutableLiveData<LocationEvent>()
    val myLocationEvent: LiveData<LocationEvent>
        get() = _myLocationEvent

    private val _dialogResponse = SingleLiveEvent<Any>()
    val dialogResponse: LiveData<Any>
        get() = _dialogResponse

    private val _sendLocationRequest = MutableLiveData<Participant.Request>()
    val sendLocationRequest: LiveData<Participant.Request>
        get() = _sendLocationRequest

    private val _attendMyMarker = MutableLiveData<Boolean>()
    val attendMyMarker: LiveData<Boolean>
        get() = _attendMyMarker

    private val _timerString = MutableLiveData<String>()
    val timerString: LiveData<String>
        get() = _timerString

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String>
        get() = _toastMsg

    private val _longPressed = MutableLiveData<Int>()
    val longPressed: LiveData<Int>
        get() = _longPressed

    private val _modifyButtonClicked = SingleLiveEvent<Any>()
    val modifyButtonClicked: LiveData<Any>
        get() = _modifyButtonClicked

    private val _deleteAppointmentClicked = MutableLiveData<Promise.Response>()
    val deleteAppointmentClicked: LiveData<Promise.Response>
        get() = _deleteAppointmentClicked

    private val _cameraMoveToTarget = MutableLiveData<LatLng>()
    val cameraMoveToTarget : LiveData<LatLng>
        get() = _cameraMoveToTarget


    val title = ObservableField<String>()
    val startDate = ObservableField<String>()
    val locationName = ObservableField<String>()
    val participantNum = ObservableField<String>()
    val lateTimeGuideOnLayout = ObservableField<String>()
    val myParticipation = ObservableField<Int>()
    val requestMillis = ObservableField<Long>()
    lateinit var countDownTimer: CountDownTimer

    init {
        _trackingMode.value = 1
        _response.value = promise
        _isSocketOpen.value = false
        _attendMyMarker.value = false
        _isArrive.value = false
        _participants.value = emptyList()
        _attendedParticipants.value = emptyList()
        _myLocationEvent.value = null
        val meetingLatLng = LatLng(promise.location_lat, promise.location_lon)
        _meetingLocation.postValue(meetingLatLng)
        initRoomInfo()
//        fetchParticipants()
        setupTimer()
    }

    override fun onCleared() {
        super.onCleared()
        socketDisconnect()
    }

    fun updateResponseData(response: Promise.Response) {
        _response.value = response
        initRoomInfo()
    }

    private fun initRoomInfo() {
        title.set(_response.value!!.title)
        startDate.set("" + (_response.value!!.datetime.month + 1) + "월 " + _response.value!!.datetime.date + "일 " + _response.value!!.datetime.hours + "시 " + StringUtil.addPaddingIfSingleDigit(_response.value!!.datetime.minutes) + "분")
        locationName.set((_response.value!!.location_name))
        lateTimeGuideOnLayout.set(String.format(PromissuApplication.instance?.getString(R.string.inviting_late_time_guide)!!, _response.value?.late_range)) //
    }

    fun onClickedCurrentLocation() {
        Timber.d("onClick: currentLocationButton")
        when {
            trackingMode.value == 1 -> _trackingMode.postValue(2)
            trackingMode.value == 2 -> _trackingMode.postValue(3)
            trackingMode.value == 3 -> _trackingMode.postValue(1)
        }
    }

    fun onClickedTargetLocation(){
        _cameraMoveToTarget.postValue(LatLng(promise.location_lat,promise.location_lon))
    }

    fun onClickedBackButton() {
        _onBackPressed.call()
    }

    fun setNaverMap(naverMap: NaverMap) {
        _naverMap.postValue(naverMap)
    }

    fun fetchParticipants() {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .getParticipants(PromissuApplication.getVersionInfo(), "Bearer " + PromissuApplication.diskCache!!.userToken, promise.id)
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
                                _participants.value = onNext.filterNot { it.participation == myParticipation.get() }.sortedWith(comparator = Participant.CompareByStatus())
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
        }
        val jsonReq = JSONObject(jsonObject.toString())

        socket.on("connect") {
            socket.emit("location.join", jsonReq)
            Timber.d("@@@Connect on")
        }

        socket.on("location.info") {
            val jsonParser = JsonParser()
            val data = jsonParser.parse("" + it[0])
            val gson = Gson()
            val locationResult = data.asJsonObject.get("result")
            val locationEvent = gson.fromJson(data.asJsonObject.get("location_event"), Array<LocationEvent>::class.java)
            val map = HashMap<Int, LocationEvent>()
            locationEvent.forEach { event ->
                map[event.partId] = event
            }
            _locationEvents.postValue(map)
            Timber.d("@@@Result: " + locationResult)
            map.forEach { map ->
                Timber.d("@@@Data: " + map.toString())
            }
        }

        socket.on("location.error") {
            Timber.d("@@@LOCATION ERROR: %s", it[0].toString())
        }
        socket.connect()

    }

    private fun socketDisconnect() {
        socket.disconnect()
    }

    fun notifyEventInfo() {
        checkIsMyData()
        updateUserMarkers()
        checkAttendedParticipants()
    }

    fun checkArrive(bool: Boolean) {
        _isArrive.postValue(bool)
    }

    private fun checkIsMyData() {
        if (_locationEvents.value!!.keys.contains(myParticipation.get())) { //내 키를 가지고 있고
            _myLocationEvent.postValue(_locationEvents.value!![myParticipation.get()]) // 내 locationEvent 데이터 갱신

            if (_locationEvents.value!![myParticipation.get()]!!.status == 1) { //내 상태가 요청이 온 상태라면
                _dialogResponse.call()
            }
            if (_locationEvents.value!![myParticipation.get()]!!.status == 4 || _locationEvents.value!![myParticipation.get()]!!.status == 5) {
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

    private fun checkAttendedParticipants() {
        val list = ArrayList<Participant.Response>()
        var partList = _participants.value
        list.add(Participant.Response(0, "empty", 0, _response.value!!.datetime, 0,""))
        _locationEvents.value!!.forEach {
            if (it.value.status == 4 || it.value.status == 5) {
                val tmpPart = Participant.Response(it.value.id, it.value.nickname, it.value.partId, it.value.timestamp, it.value.status,"")
                list.add(tmpPart)

                partList = partList!!.filterNot { o -> o.participation == it.value.partId }

            }
        }
        _participants.postValue(partList)

        _attendedParticipants.postValue(list.sortedWith(comparator = Participant.CompareByStatus()))
    }

    fun sendLocationRequest(partId: Int) {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("requester", myParticipation.get())
            addProperty("target", partId)
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.request", jsonReq)

        if (!BuildConfig.DEBUG) {
            sendEventToAnalytics(promise.id, PromissuApplication.diskCache!!.userId, "location_req")
        }
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

        if (!BuildConfig.DEBUG) {
            sendEventToAnalytics(promise.id, PromissuApplication.diskCache!!.userId, "location_res")
        }
    }

    fun sendLocationReject() {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("participation", myParticipation.get())
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.reject", jsonReq)

        if (!BuildConfig.DEBUG) {
            sendEventToAnalytics(promise.id, PromissuApplication.diskCache!!.userId, "location_reject")
        }
    }

    fun sendLocationAttend(lon: Double, lat: Double) {
        val jsonObject = JsonObject().apply {
            addProperty("appointment", promise.id)
            addProperty("participation", myParticipation.get())
            addProperty("lon", lon)
            addProperty("lat", lat)
        }
        val jsonReq = JSONObject(jsonObject.toString())
        socket.emit("location.attend", jsonReq)

        if (!BuildConfig.DEBUG) {
            sendEventToAnalytics(promise.id, PromissuApplication.diskCache!!.userId, "location_attend")
        }
    }

    private fun setupTimer() {
        val now = DateTime()
        val start = DateTime(promise.datetime)
        val betweenSeconds = Seconds.secondsBetween(now, start)
        var remainSeconds = betweenSeconds.seconds
        countDownTimer = object : CountDownTimer(3600000, 1000) {
            override fun onFinish() {
            }

            override fun onTick(millisUntilFinished: Long) {
                remainSeconds -= 1
                if (remainSeconds % 60 < 10) {
                    _timerString.postValue("" + remainSeconds / 60 + "분 0" + remainSeconds % 60 + "초 남았어요!")
                } else {
                    _timerString.postValue("" + remainSeconds / 60 + "분 " + remainSeconds % 60 + "초 남았어요!")
                }
                //
                if (remainSeconds < 0) {
                    _timerString.postValue("지각 시간이에요!")
                }
            }
        }
    }

    fun startTimer() {
        countDownTimer.start()
    }

    fun removeTimer() {
        countDownTimer.cancel()
    }

    private fun sendEventToAnalytics(room_id: Int, user_id: Long, event: String) {
        val eventParams = Bundle()
        eventParams.putInt("room_id", room_id)
        eventParams.putLong("user_id", user_id)
        PromissuApplication.firebaseAnalytics!!.logEvent("appointment_$event", eventParams)
    }

    override fun onClickInviteButton(view: View) {
        val promiseDate = DateTime(promise.datetime)

        val templateId = view.context.getString(R.string.kakaolink_template_id)

        val templateArgs = HashMap<String, String>()
        templateArgs["title"] = promise.title
        templateArgs["address"] = promise.location_name
        templateArgs["date"] = "" + promiseDate.year + "년 " +
                promiseDate.monthOfYear + "월 " +
                promiseDate.dayOfMonth + "일 " +
                StringUtil.addPaddingIfSingleDigit(promiseDate.hourOfDay) + "시 " +
                StringUtil.addPaddingIfSingleDigit(promiseDate.minuteOfHour) + "분"
        templateArgs["roomid"] = "roomid=" + promise.id

        val serverCallbackArgs = HashMap<String, String>()
        serverCallbackArgs["user_id"] = "\${current_user_id}"
        serverCallbackArgs["product_id"] = "\${shared_product_id}"

        KakaoLinkService.getInstance().sendCustom(view.context, templateId, templateArgs, serverCallbackArgs, object : ResponseCallback<KakaoLinkResponse>() {
            override fun onFailure(errorResult: ErrorResult?) {
                Timber.e(errorResult!!.exception)
            }

            override fun onSuccess(result: KakaoLinkResponse?) {
                Timber.d("onSuccess(): $result")
            }
        })

    }

    override fun onClickRequestLocation(partId: Int, nickname: String) {
//        if (isSocketOpen.value!! && partId != myParticipation.get())
//            if (_locationEvents.value!![myParticipation.get()]!!.point <= 0) {
//                _toastMsg.postValue("더 이상 위치를 요청할 수 없습니다. 요청권을 구매해주세요")
//            }
//            else {
////                _sendLocationRequest.postValue(Participant.Request(partId, nickname))
//                sendLocationRequest(partId)
//            }
    }

    override fun onLongPressed(view: View, participant: Participant.Response, isAction: Boolean, millis: Long) {
        if (_isSocketOpen.value!!) {
//            _longPressed.value = isAction
            if (isAction) {
                requestMillis.set(millis)
                _longPressed.postValue(1)
            } else {
                if (millis - requestMillis.get()!! > 2000) { // 클릭 요청 시간 충족
                    if (_locationEvents.value!![myParticipation.get()]!!.point <= 0) {
                        _longPressed.postValue(3)
                        _toastMsg.postValue("더 이상 위치를 요청할 수 없습니다. 요청권을 구매해주세요")
                        if (!BuildConfig.DEBUG) {
                            val eventParams = Bundle()
                            eventParams.putInt("room_id", _response.value!!.id)
                            eventParams.putLong("user_id", PromissuApplication.diskCache!!.userId)
                            PromissuApplication.firebaseAnalytics!!.logEvent("appointment_location_shortage", eventParams)
                        }
                    } else {
                        if (_locationEvents.value!![participant.participation]!!.status == 1) {
                            _longPressed.postValue(3)
                            _toastMsg.postValue("이미 위치 요청을 받은 사용자입니다.")
                        } else {
                            _longPressed.postValue(2)
                            sendLocationRequest(participant.participation) // 요청
                        }
                    }
                } else { // 클릭 요청 시간 미달
//                    _toastMsg.postValue("요청시간 미달")
                    _longPressed.postValue(3)
                }
            }
        }
    }

    override fun onClickModifyButton(view: View) {
        _modifyButtonClicked.call()
    }

    override fun onClickDeleteButton(promise: Promise.Response) {
        _deleteAppointmentClicked.postValue(promise)
    }
}

interface DetailEventListener {
    fun onClickInviteButton(view: View)
    fun onClickModifyButton(view: View)
    fun onLongPressed(view: View, participant: Participant.Response, isAction: Boolean, millis: Long)
    fun onClickRequestLocation(partId: Int, nickname: String)
    fun onClickDeleteButton(promise: Promise.Response)
}