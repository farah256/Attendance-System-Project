package com.example.attendanceproject.Retrofit;

import com.example.attendanceproject.Model.UserSignupRequest;
import com.example.attendanceproject.Model.UserLoginRequest;
import com.example.attendanceproject.Model.SignupResponse;
import com.example.attendanceproject.Model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApi {
    @POST("/api/req/signup")
    Call<SignupResponse> registerUser(@Body UserSignupRequest user);

    @POST("/api/req/login")
    Call<LoginResponse> login(@Body UserLoginRequest user);

    @GET("/api/{userId}")
    Call<LoginResponse> getUserProfile(@Path("userId") String userId);
}