package com.simsimhan.promissu.pastdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simsimhan.promissu.network.model.Appointment
import com.simsimhan.promissu.network.model.Promise

class DetailPastViewModelFactory(val promise: Appointment) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Appointment::class.java).newInstance(promise)
    }
}