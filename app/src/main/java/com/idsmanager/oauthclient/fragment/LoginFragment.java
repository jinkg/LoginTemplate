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
import com.idsmanager.oauthclient.activity.AccountActivity;
import com.idsmanager.oauthclient.module.User;
import com.idsmanager.oauthclient.net.IDsManagerPostRequest;
import com.idsmanager.oauthclient.net.NetService;
import com.idsmanager.oauthclient.net.RequestQueueHelper;
import com.idsmanager.oauthclient.net.response.UserResponse;
import com.idsmanager.oauthclient.utils.ToastUtil;
import com.jin.uitoolkit.util.Utils;

/**
 * Created by 雅麟 on 2015/3/22.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {
    public interface LoginSuccessCallback {
        void onLoginSuccess();
    }

    private static final String TAG = LoginFragment.class.getSimpleName();


    EditText etAccount;
    EditText etPassword;
    EditText etServer;
    Button btnLogin;

    TextInputLayout tilAccount;
    TextInputLayout tilPassword;
    TextInputLayout tilServer;

    private LoginSuccessCallback callback;

    public static LoginFragment getInstance(LoginSuccessCallback callback) {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        etAccount = (EditText) view.findViewById(R.id.login_et_account);
        etPassword = (EditText) view.findViewById(R.id.login_et_password);
        etServer = (EditText) view.findViewById(R.id.login_et_server);

        btnLogin = (Button) view.findViewById(R.id.login_btn_login);
        btnLogin.setOnClickListener(this);
        view.findViewById(R.id.login_bt_register).setOnClickListener(this);

        tilServer = (TextInputLayout) view.findViewById(R.id.login_til_server);
        tilAccount = (TextInputLayout) view.findViewById(R.id.login_til_account);
        tilPassword = (TextInputLayout) view.findViewById(R.id.login_til_password);

        view.findViewById(R.id.login_tv_request_token).setOnClickListener(this);

        etServer.setText(NetService.HTTP_URL);
    }

    @Override
    public void onStart() {
        super.onStart();
        etPassword.setText(null);
        etAccount.post(new Runnable() {
            @Override
            public void run() {
                etAccount.requestFocus();
            }
        });
    }

    @Override
    protected String getRequestTag() {
        return LoginFragment.class.getName();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv_request_token:
                if (getActivity() instanceof AccountActivity) {
                    ((AccountActivity) getActivity()).addContent(AccountActivity.OpenType.TokenLogin);
                }
                break;
            case R.id.login_btn_login:
                Utils.closeInput(getActivity());
                login();
                break;
            case R.id.login_bt_register:
                if (getActivity() instanceof AccountActivity) {
                    ((AccountActivity) getActivity()).addContent(AccountActivity.OpenType.Register);
                }
        }
    }

    private void login() {
       String username = etAccount.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getActivity(), R.string.username_empty);
            return;
        }
        showLoading();
        NetService.storeHttpUrl(getActivity().getApplicationContext(), etServer.getText().toString().trim());
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getActivity().getApplicationContext());
        final IDsManagerPostRequest<UserResponse> request = new IDsManagerPostRequest(NetService.RP_LOGIN_URL, UserResponse.class,
                NetService.login(username, password),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        loginSuccess(((UserResponse) response).detail);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginFailed(error.getMessage());
                    }
                });
        request.setTag(getRequestTag());
        requestQueue.add(request);
    }


    private void loginSuccess(User user) {
        User.storeUserInfo(getActivity().getApplicationContext(), user);
        if (this.callback != null)
            callback.onLoginSuccess();
        if (getActivity() == null)
            return;
        dismissLoading();
        ToastUtil.showToast(getActivity(), R.string.login_success);
    }

    private void loginFailed(String msg) {
        if (getActivity() == null) {
            return;
        }
        dismissLoading();
        ToastUtil.showToast(getActivity(), msg);
    }

    protected void onHttpUrlChanged() {
        String str = NetService.getHttpUrl(getActivity().getApplicationContext());
        this.etServer.setText(str);
    }
}
