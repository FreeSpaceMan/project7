package com.example.projektas7;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("home")
    Call<ResponseBody> getHome();

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(@Field("name") String name, //need to update Mainactivity code
                        @Field("surname") String surname, //need to update Mainactivity code
                        @Field("email") String email, //need to update Mainactivity code
                        @Field("username") String username,
                        @Field("password") String password);


    @FormUrlEncoded
    @POST("login")
    Call<User> login(@Field("username") String username,
                     @Field("password") String password);

    @FormUrlEncoded
    @POST("messageIn")
    Call<ResponseBody> insertMessage(@Field("message") String message,
                                     @Field("userId") String userId,
                                     @Field("username") String username,
                                     @Field("latitude") String latitude,
                                     @Field("longitude") String longitude);

}
