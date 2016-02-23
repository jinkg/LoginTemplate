package com.idsmanager.oauthclient.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;


import com.idsmanager.oauthclient.application.MyApplication;

import java.util.Map;

/**
 * Created by YaLin on 2015/9/15.
 */
public class NetService {
    static {
        init(MyApplication.getContext());
    }

    private static final String BASE_URL = "http://123.59.44.6";
    public static final String HTTP_KEY = "http";
    private static final String HTTP_SP = "http_url";
    public static String HTTP_URL;

    private static final String RP_LOGIN_SUB = "/RPServer/public/oauth/user/login";
    private static final String RP_TOKEN_LOGIN_SUB = "/RPServer/api/rp/oauth/login";
    private static final String REGISTER_URL_SUB = "/RPServer/public/oauth/user/register";

    public static String RP_LOGIN_URL;
    public static String RP_TOKEN_LOGIN_URL;
    public static String REGISTER_URL;

    private static void init(Context context) {
        getHttpUrl(context);
        RP_LOGIN_URL = HTTP_URL + RP_LOGIN_SUB;
        RP_TOKEN_LOGIN_URL = HTTP_URL + RP_TOKEN_LOGIN_SUB;
        REGISTER_URL = HTTP_URL + REGISTER_URL_SUB;
    }

    public static String getHttpUrl(Context context) {
        if (context == null) {
            HTTP_URL = BASE_URL;
        } else if (TextUtils.isEmpty(HTTP_URL)) {
            SharedPreferences sp = context.getSharedPreferences(
                    HTTP_SP, Context.MODE_PRIVATE);
            HTTP_URL = sp.getString(HTTP_KEY, BASE_URL);
        }
        return HTTP_URL;
    }

    public static SharedPreferences getNetSp(Context paramContext) {
        return paramContext.getSharedPreferences(HTTP_SP, Context.MODE_PRIVATE);
    }

    public static void storeHttpUrl(Context context, String server) {
        SharedPreferences sp = context.getSharedPreferences(
                HTTP_SP, Context.MODE_PRIVATE);
        HTTP_URL = server;
        sp.edit().putString(HTTP_KEY, server)
                .apply();
        init(context);
    }


    public static Map<String, String> getHostAuthHeader(String token) {
        Map<String, String> params = new ArrayMap<>();
        params.put("Authorization", "bearer " + token);
        return params;
    }

    public static Map<String, String> login(String username, String password) {
        Map<String, String> params = new ArrayMap<>();
        params.put("username", username);
        params.put("password", password);
        return params;
    }

    public static Map<String, String> register(String username, String password) {
        Map<String, String> params = new ArrayMap<>();
        params.put("username", username);
        params.put("password", password);
        return params;
    }
}
