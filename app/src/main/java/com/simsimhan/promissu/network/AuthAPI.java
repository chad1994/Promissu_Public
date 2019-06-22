package com.simsimhan.promissu.network;

import com.simsimhan.promissu.network.model.Appointment;
import com.simsimhan.promissu.network.model.FcmToken;
import com.simsimhan.promissu.network.model.Participant;
import com.simsimhan.promissu.network.model.Promise;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthAPI {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @POST("user/login")
    Observable<Login.Response> loginKakao(@Header("Version") String string, @Body Login.Request loginRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @PUT("user/fcm/token")
    Observable<ResponseBody> updateFcmToken(@Header("Version") String string, @Header("Authorization") String token, @Body FcmToken fcm_token);


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @POST("appointment")
    Observable<Promise.Response> createPromise(@Header("Version") String string, @Header("Authorization") String token, @Body Promise.Request promiseRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @POST("participation/invite")
    Observable<Promise.Response> inviteFriends(@Header("Version") String string, @Header("Authorization") String token, @Body Promise.Request promiseRequest);


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @DELETE("appointment/{room_id}")
    Observable<Response<ResponseBody>> deleteAppointment(@Header("Version") String string, @Header("Authorization") String token, @Path("room_id") int roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @DELETE("participation/{room_id}")
    Observable<Response<ResponseBody>> leftAppointment(@Header("Version") String string, @Header("Authorization") String token, @Path("room_id") int roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @GET("appointments")
    Observable<List<Appointment>> getMyPromise(@Header("Version") String string, @Header("Authorization") String token, @Query("offset") int offset, @Query("limit") int limit, @Query("type") String type);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @POST("participation/{room_id}")
    Observable<Promise.Response> enterPromise(@Header("Version") String string, @Header("Authorization") String token, @Path("room_id") String roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @GET("appointment/{room_id}/participants")
    Observable<List<Participant.Response>> getParticipants(@Header("Version") String string, @Header("Authorization") String token, @Path("room_id") int roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
            "Platform: Android"
    })
    @PUT("appointment/{room_id}")
    Observable<Promise.Response> modifyPromise(@Header("Version") String string, @Header("Authorization") String token, @Path("room_id") int roomId, @Body Promise.Request promiseRequest);

}
