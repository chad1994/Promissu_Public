package com.simsimhan.promissu.map.search;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface DaumAPI {
    // https://dapi.kakao.com/v2/local/search/keyword.json?y=37.514322572335935&x=127.06283102249932&radius=20000&=강남역
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @GET("local/search/keyword.json?radius=20000")
    Observable<Document> searchMapWithKeyword(@Header("Authorization") String daumToken,
                                                          @Query("y") double lan,
                                                          @Query("x") double lon,
                                                          @Query("query") String query);


}
