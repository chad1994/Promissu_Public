package com.simsimhan.promissu.network;

import com.simsimhan.promissu.network.model.Promise;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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
}
