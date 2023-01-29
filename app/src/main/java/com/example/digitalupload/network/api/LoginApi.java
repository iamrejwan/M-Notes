package com.example.digitalupload.network.api;

import com.example.digitalupload.models.Auth;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LoginApi {
    @GET("ffile")
    Call<Auth> getAuth();
}
