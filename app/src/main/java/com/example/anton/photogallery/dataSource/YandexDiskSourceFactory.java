package com.example.anton.photogallery.dataSource;


import android.arch.paging.DataSource;
import android.util.Log;

import com.example.anton.photogallery.model.Photo;

import okhttp3.OkHttpClient;

/**
 * Created by anton on 17.04.2018.
 */

public class YandexDiskSourceFactory implements DataSource.Factory<Integer, Photo> {

    private final OkHttpClient okHttpClient;
    private String TAG = "MyLog";

    public YandexDiskSourceFactory(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public DataSource<Integer, Photo> create() {
        Log.d(TAG, "create YandexDataSource from factory");
        return new YandexDiskDataSource(okHttpClient);
    }
}
