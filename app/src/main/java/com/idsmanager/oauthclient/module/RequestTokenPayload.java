package com.idsmanager.oauthclient.module;

import com.google.gson.Gson;

/**
 * Created by YaLin on 2016/2/18.
 */
public class RequestTokenPayload {
    public String rpUsername;
    public String facetId;
    public String tenantId;

    public RequestTokenPayload setRpUsername(String rpUsername) {
        this.rpUsername = rpUsername;
        return this;
    }

    public RequestTokenPayload setFacetId(String facetId) {
        this.facetId = facetId;
        return this;
    }

    public RequestTokenPayload setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String toPayload() {
        return new Gson().toJson(this);
    }
}
