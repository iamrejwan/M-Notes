package com.example.digitalupload.network.api;

import com.example.digitalupload.models.CategoryModel;
import com.example.digitalupload.models.CommonModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApi {

    @GET("category")
    Call<CommonModel> getHomeCategory();
}
