package com.dinkar.blescanner.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface pscoll {

    @GET("findpassword/")
    Call<String> getMember(
            @Query("phonenumber") String UserID);
}
