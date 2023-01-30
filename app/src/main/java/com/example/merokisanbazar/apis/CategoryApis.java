package com.example.merokisanbazar.apis;

import com.example.merokisanbazar.model.Category;
import com.example.merokisanbazar.response.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CategoryApis {
    @POST("category/insert")
    Call<Category> addCategory(@Body com.example.merokisanbazar.model.Category category, @Header("Authorization") String token);

    @GET("category/all")
    Call<List<Category>> getAllCategories();

    @DELETE("category/{id}")
    Call<RegisterResponse> deleteCategory(@Header("Authorization") String token, @Path("id") String id);

    @GET("category/name/{catName}")
    Call<Category> getCatByName(@Header("Authorization") String token, @Path("catName") String catName);

    @GET("/category/{categoryId}")
    Call<Category> getCatById(@Path("categoryId") String categoryId);

}
