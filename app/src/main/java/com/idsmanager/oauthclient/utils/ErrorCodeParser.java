package com.idsmanager.oauthclient.utils;


import com.idsmanager.oauthclient.R;
import com.idsmanager.ssosublibrary.interfaces.ErrorConstants;

/**
 * Created by YaLin on 2015/12/2.
 */
public class ErrorCodeParser {
    public static int parseError(int errorCode) {
        switch (errorCode) {
            case ErrorConstants.ERROR_HOST_NOT_LOGIN:
                return R.string.request_rp_token_failed_not_login;
            case ErrorConstants.ERROR_HOST_LOGIN_TIMEOUT:
                return R.string.host_login_timeout;
            case ErrorConstants.ERROR_REQUEST_TOKEN_ILLEGAL:
                return R.string.rp_user_not_belong_host;
            case ErrorConstants.ERROR_GET_TOKEN_SERVER_ERROR:
                return R.string.request_token_server_error;
            case ErrorConstants.ERROR_FACET_ID_ERROR:
                return R.string.facet_id_error;
            case ErrorConstants.ERROR_GET_RP_TOKEN_FAILED:
                return R.string.request_token_error;
            default:
                return R.string.unknown_error;
        }
    }
}
