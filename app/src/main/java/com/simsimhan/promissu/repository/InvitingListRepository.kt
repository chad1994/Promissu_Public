package com.simsimhan.promissu.repository

import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.AppointmentResponse
import com.simsimhan.promissu.network.model.Participant
import io.reactivex.Observable

interface InvitingListRepository{
    fun getInvitingList(version : String, token : String) : Observable<AppointmentResponse>
}

class InvitingListRepositoryImpl(
        private val api : AuthAPI
) : InvitingListRepository{

    override fun getInvitingList(version: String, token: String): Observable<AppointmentResponse> {
        return api.getInvitingPromise(version, token, 0, 99)
    }
}