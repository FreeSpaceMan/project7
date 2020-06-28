package com.example.projektas7;

import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitHelper {

    public Retrofit retrofitHelp() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.47:80/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
        return retrofit;
    }
}
