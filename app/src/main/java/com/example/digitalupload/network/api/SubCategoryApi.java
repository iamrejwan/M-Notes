package com.example.digitalupload.network.api;

import com.example.digitalupload.models.CommonModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SubCategoryApi {

    @GET("subcategory")
    Call<CommonModel> getSubCategory();

    @GET("subcategory/category/{cat_id}")
    Call<CommonModel> getSubCategoryById(@Path("cat_id") String category_id);
}
