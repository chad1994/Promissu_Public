package com.simsimhan.promissu.repository

import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Participant
import io.reactivex.Observable

interface PendingRepository{
    fun getParticipants(version : String, token : String, roomId : Int) : Observable<List<Participant.Response>>
}

class PendingRepositoryImpl(
        private val api : AuthAPI
) : PendingRepository{

    override fun getParticipants(version : String, token : String, roomId : Int) : Observable<List<Participant.Response>> {
        return api.getParticipants(version,token,roomId)
    }

}