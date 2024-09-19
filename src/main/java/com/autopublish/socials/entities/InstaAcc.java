package com.autopublish.socials.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Transient;

import java.util.List;

public class InstaAcc {

    @JsonProperty("access_token")
    private String shortAccessToken;

    @JsonIgnore
    private String longAccessToken;

    @JsonIgnore
    private Long expiresIn;

    @JsonProperty("user_id")
    private String userId;

    @Transient
    @JsonProperty("permissions")
    private List<String> permissions;

    public InstaAcc() {

    }


    public InstaAcc(String shortAccessToken, String longAccessToken, Long expiresIn, String userId, List<String> permissions) {
        this.shortAccessToken = shortAccessToken;
        this.longAccessToken = longAccessToken;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.permissions = permissions;
    }

    public String getShortAccessToken() {
        return shortAccessToken;
    }

    public void setShortAccessToken(String shortAccessToken) {
        this.shortAccessToken = shortAccessToken;
    }

    public String getLongAccessToken() {
        return longAccessToken;
    }

    public void setLongAccessToken(String longAccessToken) {
        this.longAccessToken = longAccessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
