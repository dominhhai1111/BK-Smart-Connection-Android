package com.example.administrator.bk_smart_connection_android;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by DOANBK on 12/15/2017.
 */

public class ControlData {
    public static final String BASE_URL="http://dominhhhaiapps.com";
    private IApi api;
    List<ItemSong> itemSongs;


    public ControlData() {
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(IApi.class);
    }

    public List<ItemSong> getListMusic(String analyzedObject, String reObject){
//         List<ItemSong> itemSongs= new ArrayList<>();
//        api.getMusic(analyzedObject,reObject).enqueue(new Callback<List<ItemSong>>() {
//            @Override
//            public void onResponse(Call<List<ItemSong>> call, Response<List<ItemSong>> response) {
//                itemSongs = response.body();
//            }
//
//            @Override
//            public void onFailure(Call<List<ItemSong>> call, Throwable t) {
//
//            }
//        });
        return itemSongs;
    }

}
