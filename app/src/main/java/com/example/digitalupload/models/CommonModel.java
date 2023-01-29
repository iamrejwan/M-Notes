package com.example.digitalupload.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonModel {



    @SerializedName("data")
    @Expose
    private List<CategoryModel> categoryModelsList;

    public List<CategoryModel> getCategoryModelsList() {
        return categoryModelsList;
    }

    public void setCategoryModelsList(List<CategoryModel> categoryModelsList) {
        this.categoryModelsList = categoryModelsList;
    }
}
