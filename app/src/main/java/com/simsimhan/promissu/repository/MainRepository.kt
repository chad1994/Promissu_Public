package com.simsimhan.promissu.repository

import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.Login
import com.simsimhan.promissu.network.model.AppointmentResponse
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.network.model.PromiseResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response

interface MainRepository {
    fun getImminentsPromise(version : String, token : String) : Observable<AppointmentResponse>
    fun getPendingPromise(version : String, token : String) : Observable<AppointmentResponse>
    fun getInvitingListCount(version : String, token : String) : Observable<Int>
    fun enterAppointment(version : String, token : String, roomId : String) : Observable<Promise.Response>
}

class MainRepositoryImpl(
        private val api : AuthAPI
) : MainRepository{

    override fun getImminentsPromise(version : String,token : String): Observable<AppointmentResponse> {
        return api.getImminentsPromise(version,token)
    }

    override fun getPendingPromise(version: String, token: String): Observable<AppointmentResponse> {
        return api.getPendingPromise(version,token,0,99)
    }

    override fun getInvitingListCount(version: String, token: String): Observable<Int> {
        return api.getInvitingPromiseCount(version,token)
    }

    override fun enterAppointment(version: String, token: String, roomId: String): Observable<Promise.Response> {
        return api.enterPromise(version,token,roomId)
    }
}