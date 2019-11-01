package com.example.simplereddit.utils.webService;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class WebService {
    private final String BASE_URL = "https://guarded-savannah-22827.herokuapp.com";
    private final int TIMEOUT = 2000; // 2s
    private OkHttpClient client;

    public WebService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    // http get
    public Result get(String route) {
        Result result = new Result();

        String link = BASE_URL + route;
        Timber.d("API: %s", link);

        // define request
        Request request = new Request.Builder()
                .url(link)
                .build();

        try {
            Response httpResponse = client.newCall(request).execute();

            String data = httpResponse.body().string();

            if (httpResponse.isSuccessful()) {
                result.setSuccess(data);
            } else {
                result.setFail(data);
            }
        } catch (Exception e) {
            result.setFail(e.getMessage());
        }

        return result;
    }

    // http post
    public Result post(String route, @NonNull RequestParams requestParams) {
        Result result = new Result();

        String link = BASE_URL + route;
        Timber.d("API: %s", requestParams.buildUrl(link));

        // define request
        Request request = new Request.Builder()
                .url(link)
                .post(requestParams.buildRequestBody())
                .build();

        try {
            Response httpResponse = client.newCall(request).execute();

            String data = httpResponse.body().string();

            if (httpResponse.isSuccessful()) {
                result.setSuccess(data);
            } else {
                result.setFail(data);
            }
        } catch (Exception e) {
            result.setFail(e.getMessage());
        }

        return result;
    }

    // http put
    public Result put(String route, @NotNull RequestParams requestParams) {
        Result result = new Result();

        String link = BASE_URL + route;
        Timber.d("API: %s", requestParams.buildUrl(link));

        // define request
        Request request = new Request.Builder()
                .url(link)
                .put(requestParams.buildRequestBody())
                .build();

        try {
            Response httpResponse = client.newCall(request).execute();

            String data = httpResponse.body().string();

            if (httpResponse.isSuccessful()) {
                result.setSuccess(data);
            } else {
                result.setFail(data);
            }
        } catch (Exception e) {
            result.setFail(e.getMessage());
        }

        return result;
    }
}
