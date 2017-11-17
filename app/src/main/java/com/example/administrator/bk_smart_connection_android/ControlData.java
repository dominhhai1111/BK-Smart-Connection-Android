package com.example.administrator.bk_smart_connection_android;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by DOANBK on 11/16/2017.
 */

public class ControlData {

    private Api api;
    private User user;

    public ControlData() {
        user = new User();
        api = new Retrofit.Builder()
                .baseUrl("http://dominhhhaiapps.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(Api.class);

        api.getUser()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                               @Override
                               public void accept(User u) throws Exception {
                                   user = u;
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
    }

    public User getUser() {

        return user;
    }
}
