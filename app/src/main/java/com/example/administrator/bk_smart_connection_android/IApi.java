package com.example.administrator.bk_smart_connection_android;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by DOANBK on 11/17/2017.
 */

public interface IApi {

    @GET("/test/{object1}/{object2}")
    Call<List<ItemSong>> getMusic(@Path("object1") String analyzedObject, @Path("object2") String reObject);

//    @GET("")
//    Call<ItemSong> getMusic(@Path("object1") String analyzedObject, @Path("object2") String reObject);


}