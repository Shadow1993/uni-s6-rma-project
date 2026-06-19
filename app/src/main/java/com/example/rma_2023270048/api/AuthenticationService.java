package com.example.rma_2023270048.api;

import com.example.rma_2023270048.models.Auth;
import com.example.rma_2023270048.models.ChangePassword;
import com.example.rma_2023270048.models.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthenticationService {

    @POST("auth/login")
    Call<Token> login(@Body Auth credentials);

    @POST("auth/register")
    Call<Void> register(@Body Auth credentials);

    @PUT("auth/changepassword")
    Call<Void> changePassword(@Body ChangePassword changePassword);

}
