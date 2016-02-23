package com.idsmanager.oauthclient.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.idsmanager.oauthclient.R;
import com.idsmanager.oauthclient.constants.Constants;
import com.idsmanager.oauthclient.fragment.LoginFragment.LoginSuccessCallback;
import com.idsmanager.oauthclient.log.StatLog;
import com.idsmanager.oauthclient.module.RequestTokenPayload;
import com.idsmanager.oauthclient.module.User;
import com.idsmanager.oauthclient.net.IDsManagerGetRequest;
import com.idsmanager.oauthclient.net.NetService;
import com.idsmanager.oauthclient.net.RequestQueueHelper;
import com.idsmanager.oauthclient.net.response.UserResponse;
import com.idsmanager.oauthclient.utils.ErrorCodeParser;
import com.idsmanager.oauthclient.utils.ToastUtil;
import com.idsmanager.ssosublibrary.RpSSOApi;
import com.idsmanager.ssosublibrary.data.RPToken;
import com.idsmanager.ssosublibrary.interfaces.TokenResultCallback;
import com.jin.uitoolkit.util.Utils;

/**
 * Created by 雅麟 on 2015/3/22.
 */
public class TokenLoginFragment extends BaseFragment implements View.OnClickListener, TokenResultCallback {
    private static final String TAG = TokenLoginFragment.class.getSimpleName();

    EditText etAccount;
    EditText etServer;
    Button btnLogin;

    TextInputLayout tilAccount;
    TextInputLayout tilServer;

    private LoginSuccessCallback callback;

    public static TokenLoginFragment getInstance(LoginSuccessCallback callback) {
        TokenLoginFragment fragment = new TokenLoginFragment();
        fragment.callback = callback;
        return fragment;
    }

    public static void open(int container, FragmentManager manager, LoginSuccessCallback callback) {
        if (manager.findFragmentByTag(TAG) != null) {
            return;
        }
        manager.beginTransaction().setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out,
                R.anim.push_right_in,
                R.anim.push_right_out)
                .add(container, getInstance(callback), TAG)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public View createContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_token_login, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        etAccount.post(new Runnable() {
            @Override
            public void run() {
                etAccount.requestFocus();
            }
        });
    }

    private void initView(View view) {
        etAccount = (EditText) view.findViewById(R.id.token_login_et_account);
        etServer = (EditText) view.findViewById(R.id.token_login_et_server);

        btnLogin = (Button) view.findViewById(R.id.token_login_btn_login);
        btnLogin.setOnClickListener(this);

        tilServer = (TextInputLayout) view.findViewById(R.id.token_login_til_server);
        tilAccount = (TextInputLayout) view.findViewById(R.id.token_login_til_account);
        this.etServer.setText(NetService.HTTP_URL);

    }


    @Override
    protected String getRequestTag() {
        return TokenLoginFragment.class.getName();
    }

    @Override
    public void onClick(View v) {
        String username = etAccount.getText().toString().trim();
        switch (v.getId()) {
            case R.id.token_login_btn_login:
                Utils.closeInput(getActivity());
                requestTokenLogin(username);
                break;
        }
    }

    public void requestTokenLogin(String username) {
        StatLog.printLog(TAG, getActivity().getLocalClassName());
        if (TextUtils.isEmpty(username)) {
            ToastUtil.showToast(getActivity(), R.string.username_empty);
            return;
        }
        NetService.storeHttpUrl(getActivity().getApplicationContext(), this.etServer.getText().toString().trim());
        RequestTokenPayload requestTokenPayload = new RequestTokenPayload();
        requestTokenPayload.setRpUsername(username)
                .setFacetId(RpSSOApi.getFacetId())
                .setTenantId(Constants.tenantId);
        RpSSOApi.requestTokenShareData(requestTokenPayload.toPayload(), this);
    }

    @Override
    public void requestSuccess(RPToken rpToken) {
        tokenLogin(rpToken.getRpToken());
    }

    @Override
    public void requestError(int errorCode) {
        dismissLoading();
        ToastUtil.showToast(getActivity(), ErrorCodeParser.parseError(errorCode));
    }

    private void tokenLogin(String token) {
        showLoading();
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getActivity());
        IDsManagerGetRequest<UserResponse> request = new IDsManagerGetRequest<>(NetService.RP_TOKEN_LOGIN_URL, UserResponse.class, NetService.getHostAuthHeader(token),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        ToastUtil.showToast(getActivity(), R.string.login_success);
                        User user = ((UserResponse) response).detail;
                        loginSuccess(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginFailed(error.getMessage());
                    }
                });
        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void loginSuccess(User user) {
        User.storeUserInfo(getActivity().getApplicationContext(), user);
        if (callback != null) {
            callback.onLoginSuccess();
        }
        if (getActivity() == null) {
            return;
        }
        dismissLoading();
        ToastUtil.showToast(getActivity(), R.string.login_success);
        getActivity().finish();
    }

    private void loginFailed(String msg) {
        if (getActivity() == null) {
            return;
        }
        dismissLoading();
        ToastUtil.showToast(getActivity(), msg);
    }

    @Override
    protected void onHttpUrlChanged() {
        String str = NetService.getHttpUrl(getActivity().getApplicationContext());
        this.etServer.setText(str);
    }
}
