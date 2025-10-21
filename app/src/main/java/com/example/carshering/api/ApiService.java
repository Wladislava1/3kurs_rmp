package com.example.carshering.api;

import com.example.carshering.model.User;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import com.example.carshering.model.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/login")
    Call<ResponseBody> login(@Body User user);

    @POST("api/register")
    Call<ResponseBody> register(@Body User user);

    @GET("api/cars")
    Call<List<Car>> getCars();

    @GET("api/cars/search")
    Call<List<Car>> searchCars(@Query("query") String query);

    @GET("api/user")
    Call<User> getUser(@Query("email") String email);

    @Multipart
    @POST("api/user/photo")
    Call<ResponseBody> uploadProfilePhoto(@Query("email") String email,
                                          @Part MultipartBody.Part file);
}
