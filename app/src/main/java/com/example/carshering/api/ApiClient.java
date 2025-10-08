package com.example.carshering.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofit() {
        if (retrofit == null) { // чтобы не создавать каждый раз заново
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // JSON-ответ сервера → Java-класс
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofit().create(ApiService.class);
    }
}
