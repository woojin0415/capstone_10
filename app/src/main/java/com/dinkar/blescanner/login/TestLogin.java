package com.dinkar.blescanner.login;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TestLogin {

    @GET("signup/")
    Call<String> getMember(
            @Query("phonenumber") String phonenumber,
            @Query("password") String password,
            @Query("type") String type);
}