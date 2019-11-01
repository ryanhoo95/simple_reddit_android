package com.example.simplereddit.utils.webService;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

public class RequestParams {
    private HashMap<String, Object> map;

    public RequestParams() {
        map = new HashMap<>();
    }

    public void put(String key, String value) {
        if (value == null) {
            map.put(key, "");
        } else {
            map.put(key, value);
        }
    }

    public void put(String key, int value) {
        try {
            map.put(key, String.valueOf(value));
        } catch (Exception e) {
            map.put(key, "");
        }
    }

    public void put(String key, long value) {
        try {
            map.put(key, String.valueOf(value));
        } catch (Exception e) {
            map.put(key, "");
        }
    }

    public void put(String key, double value) {
        try {
            map.put(key, String.valueOf(value));
        } catch (Exception e) {
            map.put(key, "");
        }
    }

    public void put(String key, float value) {
        try {
            map.put(key, String.valueOf(value));
        } catch (Exception e) {
            map.put(key, "");
        }
    }

    public void put(String key, boolean value) {
        try {
            map.put(key, String.valueOf(value));
        } catch (Exception e) {
            map.put(key, "");
        }
    }

    public RequestBody buildRequestBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        // just set a dummy entry
        if (map.isEmpty()) {
            map.put("dummy", "");
        }

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
        }

        return builder.build();
    }

    public String buildUrl(String url) {
        try {
            HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }

            return builder.build().toString();

        } catch (Exception e) {
            Timber.e(e);
            return "";
        }
    }
}
