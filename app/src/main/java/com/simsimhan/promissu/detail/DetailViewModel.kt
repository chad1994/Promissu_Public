package com.simsimhan.promissu.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import timber.log.Timber

class DetailViewModel(val promise: Promise.Response) : BaseViewModel() {

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

        val meetingLatLng = LatLng(promise.location_lon.toDouble(), promise.location_lat.toDouble())
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
                Participant.Response(2, "영니"))
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


}