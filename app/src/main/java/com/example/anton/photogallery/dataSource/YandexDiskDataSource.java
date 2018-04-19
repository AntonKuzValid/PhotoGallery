package com.example.anton.photogallery.dataSource;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.anton.photogallery.model.Photo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by anton on 12.04.2018.
 */

public class YandexDiskDataSource extends PositionalDataSource<Photo> {

    OkHttpClient okHttpClient;
    private static final String TAG = "MyLog";
    private String url = "https://cloud-api.yandex.net/v1/disk/resources/files?media_type=image";

    public YandexDiskDataSource(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams loadInitialParams, @NonNull final LoadInitialCallback<Photo> loadInitialCallback) {
        Log.d(TAG, "loadInitial() called with: loadInitialParams = [" + loadInitialParams + "], loadInitialCallback = [" + loadInitialCallback + "]");
        Request request = new Request.Builder()
                .url(setParams(url, loadInitialParams.requestedStartPosition, loadInitialParams.requestedLoadSize))
                .build();

        Log.d(TAG, "call to [" + url + "]");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], e = [" + e + "]");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response is succsessfull");
                    List<Photo> photos = unmarshal(response.body().string());
                    loadInitialCallback.onResult(photos, 0);
                } else {
                    Log.d(TAG, "Response is not successfull - " + response.message());
                }
            }
        });
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams
                                  loadRangeParams, @NonNull final LoadRangeCallback<Photo> loadRangeCallback) {
        Log.d(TAG, "loadRange() called with: loadRangeParams = [" + loadRangeParams + "], loadRangeCallback = [" + loadRangeCallback + "]");
        Request request = new Request.Builder()
                .url(setParams(url, loadRangeParams.startPosition, loadRangeParams.loadSize))
                .build();

        Log.d(TAG, "call to [" + url + "]");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure() called with: call = [" + call + "], e = [" + e + "]");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response is succsessfull");
                    List<Photo> photos = unmarshal(response.body().string());
                    loadRangeCallback.onResult(photos);
                } else {
                    Log.d(TAG, "Response is not successfull - " + response.message());
                }
            }
        });
    }

    private String setParams(String url, int offset, int limit) {
        Log.d(TAG, "setParams() called with: url = [" + url + "], offset = [" + offset + "], limit = [" + limit + "]");
        return String.format("%s&offset=%s&limit=%s", url, offset, limit);
    }

    private List<Photo> unmarshal(String response) {
        Log.d(TAG, "unmarshal() called with: response = [" + response + "]");
        List<Photo> photos = new ArrayList<>();
        try {
            JSONArray items = new JSONObject(response).getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject photoJson = items.getJSONObject(i);
                photos.add(new Photo(
                        photoJson.getString("resource_id"),
                        photoJson.getString("preview"),
                        photoJson.getString("file")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "unmarshal: fail with exception - {}", e);
        }
        return photos;
    }
}
