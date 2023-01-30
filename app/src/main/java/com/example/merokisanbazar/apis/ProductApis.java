package com.example.merokisanbazar.apis;

import com.example.merokisanbazar.model.Product;
import com.example.merokisanbazar.request.Comment;
import com.example.merokisanbazar.request.Like;
import com.example.merokisanbazar.response.ImageResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProductApis {

    @Multipart
    @POST("upload")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part img);


    @GET("product/seller/showall")
    Call<List<Product>> getAllProductOfSeller(@Header("Authorization") String token);

    @POST("product/insert")
    Call<Product> addProduct(@Body Product product, @Header("Authorization") String token);

    @GET("product/showall")
    Call<List<Product>> getAllProducts();

    @GET("product/search/{query}")
    Call<List<Product>> searchProduct(@Path("query") String query);

    @GET("product/single/{pid}")
    Call<Product> getSingleProduct(@Path("pid") String pid);

    @DELETE("product/delete/{pid}")
    Call<String> deleteProduct(@Header("Authorization") String token, @Path("pid") String pid);

    @GET("product/category/{id}")
    Call<List<Product>> getProductsByCategory(@Path("id") String id);

    @PUT("product/sell/{id}")
    Call<String> sellProduct(@Path("id") String id, @Body Product product);

    @PUT("product/like")
    Call<Product> likeProduct(@Header("Authorization") String token, @Body Like like);

    @PUT("product/unlike")
    Call<Product> unLikeProduct(@Header("Authorization") String token, @Body Like like);

    @PUT("product/comment")
    Call<Product> comment(@Header("Authorization") String token, @Body Comment comment);


    @PUT("product/uncomment")
    Call<Product> unComment(@Header("Authorization") String token, @Body Comment comment);

    @PUT("product/update/image/{pid}")
    Call<String> updateProductImage(@Header("Authorization") String token, @Path("pid") String pid, @Body Product product);

    @PUT("product/update/{pid}")
    Call<String> updateProduct(@Header("Authorization") String token, @Path("pid") String pid, @Body Product product);
}
