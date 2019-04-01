package com.simsimhan.promissu.promise.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.util.SingleLiveEvent
import timber.log.Timber
import java.util.*

class CreateViewModel : BaseViewModel(), CreateEventListener {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    private val _detail = MutableLiveData<String>()
    val detail: LiveData<String>
        get() = _detail

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
    val locationName : LiveData<String>
        get() = _locationName

    private val _onClickedCreateButton = SingleLiveEvent<Any>()
    val onClickedCreateButton : LiveData<Any>
        get() = _onClickedCreateButton

    private val _selectedLocation = MutableLiveData<Marker>()
    val selectedLocation : LiveData<Marker>
        get() = _selectedLocation

    private val _selectedInfo = MutableLiveData<InfoWindow>()
    val selectedInfo : LiveData<InfoWindow>
        get() = _selectedInfo


    init {

    }

    fun onClickCreateBtn(){
        _onClickedCreateButton.call()
        Timber.d("@@@Clicked Create"+title.value)
    }

    fun setCreateInfo(lat:Double,lon:Double,location:String,locationName:String){
        _lat.postValue(lat)
        _lon.postValue(lon)
        _location.postValue(location)
        _locationName.postValue(locationName)
    }

    fun createValidation(){

    }

    override fun onTextChanged(s: String) {
        _title.postValue(s)
        Timber.d("@@@TextChanged "+ s)
    }
}

interface CreateEventListener {

    fun onTextChanged(s: String)
}