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
            "Content-Type: application/json"
    })
    @POST("user/login")
    Observable<Login.Response> loginKakao(@Body Login.Request loginRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @PUT("user/fcm/token")
    Observable<ResponseBody> updateFcmToken(@Header("Authorization") String token, @Body FcmToken fcm_token);


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("appointment")
    Observable<Promise.Response> createPromise(@Header("Authorization") String token, @Body Promise.Request promiseRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("participation/invite")
    Observable<Promise.Response> inviteFriends(@Header("Authorization") String token, @Body Promise.Request promiseRequest);


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @DELETE("appointment/{room_id}")
    Observable<Response<ResponseBody>> deleteAppointment(@Header("Authorization") String token, @Path("room_id") int roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @DELETE("participation/{room_id}")
    Observable<Response<ResponseBody>> leftAppointment(@Header("Authorization") String token, @Path("room_id") int roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("appointments")
    Observable<List<Appointment>> getMyPromise(@Header("Authorization") String token, @Query("offset") int offset, @Query("limit") int limit, @Query("type") String type);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("participation/{room_id}")
    Observable<Promise.Response> enterPromise(@Header("Authorization") String token, @Path("room_id") String roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("appointment/{room_id}/participants")
    Observable<List<Participant.Response>> getParticipants(@Header("Authorization") String token, @Path("room_id") int roomId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @PUT("appointment/{room_id}")
    Observable<Promise.Response> modifyPromise(@Header("Authorization") String token,@Path("room_id") int roomId, @Body Promise.Request promiseRequest);

}
