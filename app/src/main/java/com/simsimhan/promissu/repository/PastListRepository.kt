package com.simsimhan.promissu.repository

import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.AppointmentResponse
import com.simsimhan.promissu.network.model.AttendanceResult
import io.reactivex.Observable

interface PastListRepository {
    fun getPastList(version : String , token : String) : Observable<AppointmentResponse>
    fun getAttendanceResult(version : String, token : String) : Observable<AttendanceResult>
}

class PastListRepositoryImpl(
        private val api : AuthAPI
) : PastListRepository{

    override fun getPastList(version: String, token: String): Observable<AppointmentResponse> {
        return api.getClosedPromise(version,token,0,99)
    }

    override fun getAttendanceResult(version: String, token: String): Observable<AttendanceResult> {
        return api.getAttendanceResult(version,token)
    }
}