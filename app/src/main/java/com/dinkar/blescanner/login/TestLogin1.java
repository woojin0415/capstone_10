package com.dinkar.blescanner.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestLogin1 {

    @GET("login/")
    Call<String> getMember(
            @Query("phonenumber") String UserID,
            @Query("password") String password);

}
