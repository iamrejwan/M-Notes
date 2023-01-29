package com.example.digitalupload.network.api;

import com.example.digitalupload.models.CommonModel;
import com.example.digitalupload.models.UserModel;
import com.example.digitalupload.models.UserModelList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @GET("person/")
    Call<UserModelList> getAllUser();

    @GET("person/{id}")
    Call<UserModel> getUserById(@Path("id") String user_id);

    @GET("category/{cat_id}/subcategory/{sub_id}")
    Call<UserModelList> getUserByCategory(@Path("cat_id") String cat_id, @Path("sub_id") String sub_id);

    @POST("person/search/")
    @FormUrlEncoded
    Call<UserModelList> searchUser(@Field("search") String searchText);

    @Multipart
    @POST("person/")
    Call<UserModel> addUser(@Part("name") RequestBody name,
                                       @Part("number") RequestBody email,
                                       @Part("rank") RequestBody phone,
                                       @Part("address") RequestBody password,
                                       @Part("category") RequestBody currentPassword,
                                       @Part MultipartBody.Part photo,
                                       @Part("sub_category") RequestBody gender);


    @Multipart
    @POST("person/{id}")
    Call<UserModel> updateUser(@Path("id") String user_id,
                                        @Part("name") RequestBody name,
                                        @Part("number") RequestBody email,
                                        @Part("rank") RequestBody phone,
                                        @Part MultipartBody.Part photo,
                                        @Part("address") RequestBody password);


    @DELETE("person/")
    Call<CommonModel> deleteUser(@Query("id") String user_id);
}
