package com.example.anton.photogallery;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.anton.photogallery.adapters.PhotoGalleryPagedListAdapter;
import com.example.anton.photogallery.dataSource.YandexDiskSourceFactory;
import com.example.anton.photogallery.model.Photo;
import com.yandex.authsdk.YandexAuthException;
import com.yandex.authsdk.YandexAuthOptions;
import com.yandex.authsdk.YandexAuthSdk;
import com.yandex.authsdk.YandexAuthToken;

import java.io.IOException;
import java.util.concurrent.Executors;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyLogs";
    private YandexAuthSdk sdk;
    private static final int REQUEST_CODE_YA_LOGIN = 1;
    RecyclerView recyclerView;
    PhotoGalleryPagedListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdk = new YandexAuthSdk(new YandexAuthOptions(this, true));
        Log.d(TAG, "ОТтправлен запрос на авторизацию");
        startActivityForResult(sdk.createLoginIntent(this, null), REQUEST_CODE_YA_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Получен ответ авторизации с кодом - " + resultCode);
        if (requestCode == REQUEST_CODE_YA_LOGIN) {
            try {
                final YandexAuthToken yandexAuthToken = sdk.extractToken(resultCode, data);
                Log.d(TAG, "Токен авторизации -" + yandexAuthToken);
                if (yandexAuthToken != null) {
                    setContentView(R.layout.activity_main);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
                    recyclerView = (RecyclerView) findViewById(R.id.rv_images);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(layoutManager);
                    OkHttpClient client = getClientWithHeader(yandexAuthToken.getValue());
                    YandexDiskSourceFactory factory = new YandexDiskSourceFactory(client);

                    PagedList.Config config = new PagedList.Config.Builder()
                            .setEnablePlaceholders(false)
                            .setPageSize(R.integer.pages)
                            .build();

                    LiveData<PagedList<Photo>> pagedList = new LivePagedListBuilder<>(factory, config)
                            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
                            .build();

                    adapter = new PhotoGalleryPagedListAdapter(Photo.DIFF_CALLBACK, this);

                    pagedList.observe(this, new Observer<PagedList<Photo>>() {
                        @Override
                        public void onChanged(@Nullable PagedList<Photo> photos) {
                            Log.d(TAG, "set PagedList");
                            adapter.setList(photos);
                        }
                    });
//                    adapter.setList(pagedList);
                    recyclerView.setAdapter(adapter);
                }
            } catch (YandexAuthException e) {
                Log.d(TAG, "Ошибка во время извлечения токена");
                Toast.makeText(this, "К сожалению вам не удалось атворизоваться", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private OkHttpClient getClientWithHeader(final String yandexAuthToken) {
        Log.d(TAG, "getClientWithHeader() called with: yandexAuthToken = [" + yandexAuthToken + "]");
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "OAuth " + yandexAuthToken)
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();
    }
}
