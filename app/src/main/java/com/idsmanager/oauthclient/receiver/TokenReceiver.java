package com.idsmanager.oauthclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.idsmanager.oauthclient.activity.MainActivity;
import com.idsmanager.oauthclient.module.User;
import com.idsmanager.ssosublibrary.data.RPToken;
import com.idsmanager.ssosublibrary.interfaces.Constants;

/**
 * Created by YaLin on 2015/11/24.
 */

public class TokenReceiver extends BroadcastReceiver {
    private static final String TAG = TokenReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Constants.ACTION_RP_TOKEN_RECEIVED.equals(action)) {
            RPToken token = RPToken.fromJson(intent.getStringExtra(Constants.TOKEN_KEY));
            MainActivity.open(context, token.getRpToken());
        } else if (Constants.ACTION_RP_SLO.equals(action)) {
            User.deleteUserInfo(context);
        }
    }
}
