package com.example.projektas7;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    @GET("home")
    Call<ResponseBody> getHome();
}
