package com.idsmanager.oauthclient.application;

import android.app.Application;
import android.content.Context;

import com.idsmanager.ssosublibrary.RpSSOApi;


/**
 * Created by YaLin on 2015/12/11.
 */
public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        RpSSOApi.init(getApplicationContext());
    }

    public static Context getContext() {
        return mContext;
    }
}
