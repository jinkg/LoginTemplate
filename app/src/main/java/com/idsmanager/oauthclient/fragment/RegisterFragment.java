package com.idsmanager.oauthclient.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.idsmanager.oauthclient.module.User;
import com.idsmanager.oauthclient.net.IDsManagerPostRequest;
import com.idsmanager.oauthclient.net.NetService;
import com.idsmanager.oauthclient.net.RequestQueueHelper;
import com.idsmanager.oauthclient.net.response.UserResponse;
import com.idsmanager.oauthclient.utils.ToastUtil;
import com.jin.uitoolkit.util.Utils;

/**
 * Created by wind on 2016/1/5.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = RegisterFragment.class.getSimpleName();

    EditText mEtAccount;
    EditText mEtPassword;
    Button mBtnRegister;
    EditText etServer;

    private LoginFragment.LoginSuccessCallback callback;

    public static RegisterFragment getInstance(LoginFragment.LoginSuccessCallback callback) {
        RegisterFragment fragment = new RegisterFragment();
        fragment.callback = callback;
        return fragment;
    }

    public static void open(int container, FragmentManager manager, LoginFragment.LoginSuccessCallback callback) {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        mEtAccount.post(new Runnable() {
            @Override
            public void run() {
                mEtAccount.requestFocus();
            }
        });
        String server = NetService.getHttpUrl(getActivity().getApplicationContext());
        if (!TextUtils.isEmpty(server)) {
            etServer.setText(server);
        }
    }

    @Override
    protected String getRequestTag() {
        return null;
    }

    void initView(View view) {
        etServer = (EditText) view.findViewById(R.id.register_et_server);
        mEtAccount = (EditText) view.findViewById(R.id.register_et_account);
        mEtPassword = (EditText) view.findViewById(R.id.register_et_password);
        mBtnRegister = (Button) view.findViewById(R.id.register_btn_register);
        mBtnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_register:
                Utils.closeInput(getActivity());
                register();
                break;
        }
    }

    private void register() {
        String username = mEtAccount.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        if (!checkData(username, password)) {
            return;
        }
        showLoading();
        NetService.storeHttpUrl(getActivity().getApplicationContext(), etServer.getText().toString().trim());
        RequestQueue requestQueue = RequestQueueHelper.getInstance(getActivity().getApplicationContext());
        IDsManagerPostRequest<UserResponse> request = new IDsManagerPostRequest<>(NetService.REGISTER_URL, UserResponse.class, null, NetService.register(username, password),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        loginSuccess(((UserResponse) response).detail);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        registerError(error.getMessage());
                    }
                }
        );
        request.setTag(getRequestTag());
        requestQueue.add(request);
    }


    private boolean checkData(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtil.showToast(getActivity(), R.string.username_empty);
            return false;
        }
        return true;
    }

    private void registerError(String errorMsg) {
        if (getActivity() == null) {
            return;
        }
        dismissLoading();
        ToastUtil.showToast(getActivity(), errorMsg);
    }

    private void loginSuccess(User user) {
        User.storeUserInfo(getActivity().getApplicationContext(), user);
        if (this.callback != null) {
            callback.onLoginSuccess();
        }
        if (getActivity() == null) {
            return;
        }
        dismissLoading();
        ToastUtil.showToast(getActivity(), R.string.login_success);
        getActivity().finish();
    }

    @Override
    protected void onHttpUrlChanged() {
        String server = NetService.getHttpUrl(getActivity().getApplicationContext());
        etServer.setText(server);
    }
}
