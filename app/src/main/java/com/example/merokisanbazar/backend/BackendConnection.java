package com.example.merokisanbazar.backend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BackendConnection {

    public static String token = "Bearer ";

    public static final String base_url = "http://10.0.2.2:8080/";
    //public static String base_url = "http://192.168.1.69:8080/"; //your local IP
    public static String imagePath = base_url + "uploads/";


    public static Retrofit getInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
