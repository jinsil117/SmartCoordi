package com.pickth.dddd.smartcoordination;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by parkjinsil on 2019-03-21.
 */

//위도와 경도를 받아오기 위한 api 인터페이스
public interface ApiInterface {
    //openweathermap에 데이터를 받아오도록 하는
    @GET("/data/2.5/weather")
    //위도와 경도를 받아오며, CoordiRepo를 호출한다.
    Call<CoordiRepo> repo(@Query("appid") String appid, @Query("lat") double lat, @Query("lon") double lon);
}
