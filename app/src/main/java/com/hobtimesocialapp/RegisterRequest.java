package com.hobtimesocialapp;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fatih on 11.11.2014.
 */
public class RegisterRequest extends Request<String> {

    private Map<String, String> mParams;

    public RegisterRequest(int method, String url, Response.ErrorListener listener, HashMap<String, String> requestParams) {
        super(method, url, listener);
        mParams = requestParams;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String resp = new String (response.data,"UTF-8");
            Log.v("XXXXXXXXXXXXXX",resp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deliverResponse(String response) {


    }
}
