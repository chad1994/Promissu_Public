package com.simsimhan.promissu.network

import com.naver.maps.geometry.LatLng
import com.simsimhan.promissu.map.search.Document
import com.simsimhan.promissu.model.LocationSearch
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverAPI {
    @Headers("Accept: application/json", "Content-Type: application/json")
    @GET("search")
    fun searchLocationWithKeyword(@Header("X-NCP-APIGW-API-KEY-ID") naverID: String,
                                  @Header("X-NCP-APIGW-API-KEY") naverSecret: String,
                                  @Query("query") query: String,
                                  @Query("coordinate") latLng: String): Observable<LocationSearch>


}