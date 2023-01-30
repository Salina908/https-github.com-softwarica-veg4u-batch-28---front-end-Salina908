package com.example.merokisanbazar.apis;

import com.example.merokisanbazar.model.User;
import com.example.merokisanbazar.response.LoginResponse;
import com.example.merokisanbazar.response.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApis {

    @POST("buyer/login")
    Call<LoginResponse> checkUserBuyer(@Body User user);

    @POST("seller/login")
    Call<LoginResponse> checkUserSeller(@Body User user);

    @POST("admin/login")
    Call<LoginResponse> checkUserAdmin(@Body User user);

    @POST("seller/upload")
    Call<RegisterResponse> registerSeller(@Body User user);

    @POST("buyer/upload")
    Call<RegisterResponse> registerBuyer(@Body User user);

    @GET("buyer/profileByToken")
    Call<User> getBuyerUserDetails(@Header("Authorization") String token);

    @GET("seller/profileByToken")
    Call<User> getUserDetails(@Header("Authorization") String token);

    @PUT("seller/{sid}")
    Call<String> updateUser(@Header("Authorization") String token, @Path("sid") String sid, @Body User user);

    @PUT("/update/buyer/{bid}")
    Call<String> updateBuyer(@Header("Authorization") String token, @Path("bid") String bid, @Body User user);

    @GET("/seller/admin/getallUsers")
    Call<List<User>> getAllSellerUsers(@Header("Authorization") String token);

    @DELETE("seller/delete/{sid}")
    Call<String> deleteSeller(@Header("Authorization") String token, @Path("sid") String sid);

    @PUT("/seller/verify/admin/{sid}")
    Call<String> verifyUser(@Header("Authorization") String token, @Path("sid") String sid);

    @GET("seller/search/{query}")
    Call<List<User>> searchUser(@Header("Authorization") String token, @Path("query") String query);

}
