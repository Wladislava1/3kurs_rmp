package com.example.carshering.api;

import com.example.carshering.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/login")
    Call<ResponseBody> login(@Body User user);

    @POST("api/register")
    Call<ResponseBody> register(@Body User user);
}
