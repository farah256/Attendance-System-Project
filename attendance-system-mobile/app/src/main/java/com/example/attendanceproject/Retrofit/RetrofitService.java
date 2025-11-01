package com.example.attendanceproject.Retrofit;

import com.example.attendanceproject.helper.AuthInterceptor;
import com.example.attendanceproject.helper.MixedTypeConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String BASE_URL = "http://192.168.133.35:8080/";
    private Retrofit retrofit;
    private AuthInterceptor authInterceptor;

    public RetrofitService() {
        authInterceptor = new AuthInterceptor();
        initRetrofit();
    }

    private void initRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(new MixedTypeConverterFactory())
                .build();
    }

    public void setAuthToken(String token) {
        authInterceptor.setToken(token);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}