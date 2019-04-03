package com.simsimhan.promissu.promise.create

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.NavigationUtil
import com.simsimhan.promissu.util.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class CreateViewModel : BaseViewModel(), CreateEventListener {

    private lateinit var token: String

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _description = MutableLiveData<String>()
    val description: LiveData<String>
        get() = _description

    private val _startTime = MutableLiveData<Date>()
    val startTime: LiveData<Date>
        get() = _startTime

    private val _endTime = MutableLiveData<Date>()
    val endTime: LiveData<Date>
        get() = _endTime

    private val _lat = MutableLiveData<Double>()
    val lat: LiveData<Double>
        get() = _lat

    private val _lon = MutableLiveData<Double>()
    val lon: LiveData<Double>
        get() = _lon

    private val _location = MutableLiveData<String>()
    val location: LiveData<String>
        get() = _location

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String>
        get() = _locationName

    private val _onClickedCreateButton = SingleLiveEvent<Any>()
    val onClickedCreateButton: LiveData<Any>
        get() = _onClickedCreateButton

    private val _selectedLocation = MutableLiveData<Marker>()
    val selectedLocation: LiveData<Marker>
        get() = _selectedLocation

    private val _selectedInfo = MutableLiveData<InfoWindow>()
    val selectedInfo: LiveData<InfoWindow>
        get() = _selectedInfo

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String>
        get() = _toastMessage

    private val _response = MutableLiveData<Promise.Response>()
    val response: LiveData<Promise.Response>
        get() = _response

    init {
        _description.postValue("설명 없음")
        token = PromissuApplication.diskCache!!.userToken
        Timber.d("@@@Token: $token")
    }

    fun onClickCreateBtn() {

        createValidation()
    }

    fun setCreateInfo(lat: Double, lon: Double, location: String, locationName: String) {
        _lat.postValue(lat)
        _lon.postValue(lon)
        _location.postValue(location)
        _locationName.postValue(locationName)
    }

    fun setStartDateTime(date: Date) {
        _startTime.postValue(date)
    }

    fun setEndDateTime(date: Date) {
        _endTime.postValue(date)
    }

    private fun createValidation() {
        if (title.value.isNullOrEmpty()) {
            _toastMessage.postValue("방 제목을 입력해주세요")
        } else if (startTime.value == null || endTime.value == null) {
            _toastMessage.postValue("약속 시간을 확인해주세요")
        } else if (location.value.isNullOrEmpty() || locationName.value.isNullOrEmpty()) {
            _toastMessage.postValue("약속장소를 확인해주세요")
        } else {
            val request = Promise.Request(title.value, description.value, startTime.value, endTime.value, locationName.value, location.value, lon.value, lat.value)
            createRoom(request)
            _toastMessage.postValue("완료")
        }
    }

    fun createRoom(request: Promise.Request) {
        addDisposable(PromissuApplication.retrofit!!
                .create(AuthAPI::class.java)
                .createPromise("Bearer $token", request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _response.postValue(it)
                    // TODO: do something here
//                    NavigationUtil.enterRoom(CreatePromiseActivity.this, onNext);
                },{
                    Timber.e(it)
                }))
    }

    override fun onTextChanged(s: String) {
        _title.postValue(s)
        Timber.d("@@@TextChanged " + s)
    }
}

interface CreateEventListener {

    fun onTextChanged(s: String)
}