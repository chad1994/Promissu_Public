package com.simsimhan.promissu.network;

import com.simsimhan.promissu.network.model.Promise;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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
    @POST("appointment")
    Observable<Promise.Request> createPromise(@Header("Authorization") String token, @Body Promise.Request promiseRequest);



    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("appointments?offset=0&limit=3")
    Observable<List<Promise.Response>> getMyPromise(@Header("Authorization") String token, @Query("offset") int offset, @Query("limit") int limit);
}
