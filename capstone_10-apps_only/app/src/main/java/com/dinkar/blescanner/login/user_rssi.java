package com.dinkar.blescanner.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface user_rssi {

    @GET("receive/")
    Call<String> getMember(
            @Query("RSSI_1") String r1,
            @Query("RSSI_2") String r2,
            @Query("RSSI_3") String r3,
            @Query("RSSI_4") String r4,
            @Query("RSSI_5") String r5,
            @Query("phonenumber") String user_id);

}
