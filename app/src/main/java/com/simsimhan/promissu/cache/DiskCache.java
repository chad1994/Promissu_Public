package com.simsimhan.promissu.cache;

import android.content.SharedPreferences;

import com.simsimhan.promissu.BuildConfig;

import androidx.annotation.NonNull;


public class DiskCache {
    private String TAG = BuildConfig.APPLICATION_ID;
    private String USER_TOKEN_TAG = TAG = ".userToken";
    private String USER_NAME_TAG = TAG = ".userName";
    private String USER_EMAIL_TAG = TAG = ".userEmail";
    private SharedPreferences pref;


    public DiskCache(SharedPreferences preferences) {
        this.pref = preferences;
    }

    public boolean setUserToken(String token) {
        return this.pref.edit().putString(USER_TOKEN_TAG, "Bearer " + token).commit();
    }

    public boolean setUserData(String name, String email) {
        return this.pref.edit()
                .putString(USER_NAME_TAG, name)
                .putString(USER_EMAIL_TAG, email)
                .commit();
    }

    public boolean clearUserData() {
        return this.pref.edit()
                .remove(USER_TOKEN_TAG)
                .remove(USER_NAME_TAG)
                .remove(USER_EMAIL_TAG)
                .commit();

    }

    @NonNull
    public String getUserToken() {
        return this.pref.getString(USER_TOKEN_TAG, "");
    }

    @NonNull
    public String getUserName() {
        return this.pref.getString(USER_NAME_TAG, "비등록 사용자");
    }

    @NonNull
    public String getEmail() {
        return this.pref.getString(USER_EMAIL_TAG, "");
    }


}
