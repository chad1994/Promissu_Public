package com.simsimhan.promissu.repository

import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Participant
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response

interface InvitingRepository{
    fun getParticipants(version : String, token : String, roomId : Int) : Observable<List<Participant.Response>>
    fun confirmAppointment(version : String, token : String, roomId: Int) : Observable<Boolean>
    fun leftAppointment(version : String, token : String, roomId: Int) : Observable<Response<ResponseBody>>
    fun deleteAppointment(version : String, token : String, roomId: Int) : Observable<Response<ResponseBody>>
}

class InvitingRepositoryImpl(
        private val api : AuthAPI
) : InvitingRepository{

    override fun getParticipants(version : String, token : String, roomId : Int) : Observable<List<Participant.Response>> {
        return api.getParticipants(version,token,roomId)
    }

    override fun confirmAppointment(version: String, token: String, roomId: Int): Observable<Boolean> {
        return api.confirmAppointment(version,token,roomId)
    }

    override fun leftAppointment(version: String, token: String, roomId: Int) : Observable<Response<ResponseBody>> {
        return api.leftAppointment(version,token,roomId)
    }

    override fun deleteAppointment(version: String, token: String, roomId: Int): Observable<Response<ResponseBody>> {
        return api.deleteAppointment(version,token,roomId)
    }
}