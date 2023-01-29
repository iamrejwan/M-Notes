package com.example.digitalupload.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserModelList {

    @SerializedName("data")
    @Expose
    private List<UserModel> userModelsList;

    public List<UserModel> getUserModelsList() {
        return userModelsList;
    }

    public void setUserModelsList(List<UserModel> userModelsList) {
        this.userModelsList = userModelsList;
    }
}
