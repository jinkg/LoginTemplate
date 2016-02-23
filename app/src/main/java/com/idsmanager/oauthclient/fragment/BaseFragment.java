package com.idsmanager.oauthclient.fragment;

import android.content.SharedPreferences;

import com.idsmanager.oauthclient.module.User;
import com.idsmanager.oauthclient.net.NetService;
import com.jin.uitoolkit.fragment.BaseLoadingFragment;


/**
 * Created by YaLin on 2015/7/24.
 */
public abstract class BaseFragment extends BaseLoadingFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onStart() {
        super.onStart();
        User.getUserSp(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        NetService.getNetSp(getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        User.getUserSp(getActivity().getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        NetService.getNetSp(getActivity().getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (User.USERNAME_KEY.equals(key)) {
            onLoginStateChanged();
        }
        if (!NetService.HTTP_KEY.equals(key)) ;
        onHttpUrlChanged();
    }

    protected void onLoginStateChanged() {
    }

    protected void onHttpUrlChanged() {
    }
}
