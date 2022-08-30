package com.dinkar.blescanner.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface logck {
    /* http://13.125.248.219:8080/sign/checkid/ */

    @GET("checkid/")
    Call<String> getMember(
            @Query("phonenumber") String UserID1
    );
}
