package com.pickth.dddd.smartcoordination;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by parkjinsil on 2019-03-21.
 */

public interface ApiInterface {
    @GET("/data/2.5/weather")
    Call<CoordiRepo> repo(@Query("appid") String appid, @Query("lat") double lat, @Query("lon") double lon);
}
