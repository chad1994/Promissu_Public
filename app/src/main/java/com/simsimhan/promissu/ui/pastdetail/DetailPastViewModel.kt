package com.simsimhan.promissu.ui.pastdetail

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.simsimhan.promissu.BaseViewModel
import com.simsimhan.promissu.network.model.Appointment
import com.simsimhan.promissu.util.StringUtil

class DetailPastViewModel(val promise: Appointment) : BaseViewModel(){

    private val _response = MutableLiveData<Appointment>() // 전체 데이터 리스트
    val response: LiveData<Appointment>
        get() = _response

    val title = ObservableField<String>()
    val startDate = ObservableField<String>()
    val locationName = ObservableField<String>()
    val locationDetail = ObservableField<String>()

    init {
        _response.value = promise
        initRoomInfo()
    }

    private fun initRoomInfo() {
        title.set(_response.value!!.promise.title)
        startDate.set("" + (_response.value!!.promise.start_datetime.month + 1) + "월 " + _response.value!!.promise.start_datetime.date + "일 " + _response.value!!.promise.start_datetime.hours + "시 " + StringUtil.addPaddingIfSingleDigit(_response.value!!.promise.start_datetime.minutes) + "분")
        locationName.set(_response.value!!.promise.location_name)
        locationDetail.set(_response.value!!.promise.location)
    }


}