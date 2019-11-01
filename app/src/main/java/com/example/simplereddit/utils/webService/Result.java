package com.example.simplereddit.utils.webService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import timber.log.Timber;

public class Result implements Serializable {
    private boolean success;
    private String data;
    private String error;

    // success response from API
    public void setSuccess(String data) {
        success = true;
        this.data = data;
    }

    // response fail
    public void setFail(String response) {
        success = false;
        try {
            error = new JSONObject(response).getString("message");
            Timber.e("API error: %s", error);
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getData() {
        return data;
    }

    public JSONObject getDataInJSONObject() {
        try {
            return new JSONObject(data);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    public JSONArray getDataInJSONArray() {
        try {
            return new JSONArray(data);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }
}
