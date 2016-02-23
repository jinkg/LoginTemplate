package com.idsmanager.oauthclient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idsmanager.oauthclient.R;
import com.idsmanager.oauthclient.constants.Constants;
import com.idsmanager.oauthclient.module.User;
import com.idsmanager.oauthclient.net.IDsManagerGetRequest;
import com.idsmanager.oauthclient.net.NetService;
import com.idsmanager.oauthclient.net.RequestQueueHelper;
import com.idsmanager.oauthclient.net.response.UserResponse;
import com.idsmanager.oauthclient.utils.ToastUtil;
import com.idsmanager.ssosublibrary.RpSSOApi;
import com.jin.uitoolkit.util.Utils;

public class MainActivity extends BaseLoadActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TOKEN_KEY = "token";

    private TextView tvInfo;
    private ViewStub vsNotLogin;
    private LinearLayout llRoot;
    private Button emptyBtnLogin;

    public static void open(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void open(Context context, String token) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TOKEN_KEY, token);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.content_main);
        initView();
        initData();
        RpSSOApi.setIdsUrl(this, Constants.IDS_URL);
    }

    @Override
    protected void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        String token = paramIntent.getStringExtra(TOKEN_KEY);
        tokenTransaction(token);
    }

    @Override
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        initData();
        if (bundle == null) {
            String token = getIntent().getStringExtra(TOKEN_KEY);
            tokenTransaction(token);
        }
    }

    private void tokenTransaction(String token) {
        if (!TextUtils.isEmpty(token)) {
            User.deleteUserInfo(getApplicationContext());
            tokenLogin(token);
        }
    }

    @Override
    protected int getDrawerCheckId() {
        return R.id.navigation_home_page;
    }

    void initView() {
        tvInfo = (TextView) findViewById(R.id.main_tv_info);
        vsNotLogin = (ViewStub) findViewById(R.id.main_vs_not_login);
        llRoot = (LinearLayout) findViewById(R.id.main_ll_root);
    }

    private void initData() {
        if (User.isLogin(getApplicationContext())) {
            vsNotLogin.setVisibility(View.GONE);
            llRoot.setVisibility(View.VISIBLE);
            String userName = String.format(getString(R.string.welcome), User.getUserAccount(getApplicationContext()));
            String loginTimes = String.format(getString(R.string.welcome1), User.getLoginTimes(getApplicationContext()));

            tvInfo.setText(userName + "\n" + loginTimes);
        } else {
            if (emptyBtnLogin == null) {
                emptyBtnLogin = (Button) vsNotLogin.inflate().findViewById(R.id.empty_btn_login);
                emptyBtnLogin.setOnClickListener(this);
            }
            llRoot.setVisibility(View.GONE);
            vsNotLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_btn_login:
                AccountActivity.requestLogin(this, Utils.getViewCenterXY(v));
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    protected void onLoginStateChanged() {
        initData();
        super.onLoginStateChanged();
    }

    private void tokenLogin(String token) {
        showLoading();
        RequestQueue localRequestQueue = RequestQueueHelper.getInstance(this);
        IDsManagerGetRequest IDsManagerGetRequest = new IDsManagerGetRequest(NetService.RP_TOKEN_LOGIN_URL, UserResponse.class, NetService.getHostAuthHeader(token),
                new Response.Listener() {
                    public void onResponse(Object response) {
                        User user = ((UserResponse) response).detail;
                        loginSuccess(user);
                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        loginFailed(error.getMessage());
                    }
                });
        IDsManagerGetRequest.setTag(TAG);
        localRequestQueue.add(IDsManagerGetRequest);
    }

    private void loginSuccess(User paramUser) {
        User.storeUserInfo(getApplicationContext(), paramUser);
        dismissLoading();
        ToastUtil.showToast(this, R.string.login_success);
    }

    private void loginFailed(String msg) {
        dismissLoading();
        ToastUtil.showToast(this, msg);
    }
}
