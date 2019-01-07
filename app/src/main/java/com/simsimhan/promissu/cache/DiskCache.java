package com.simsimhan.promissu.cache;

import android.content.SharedPreferences;

import com.simsimhan.promissu.BuildConfig;

import androidx.annotation.NonNull;


public class DiskCache {
    private String TAG = BuildConfig.APPLICATION_ID;
    private String USER_TOKEN_TAG = TAG = ".userToken";
    private String USER_NAME_TAG = TAG = ".userName";
    private String USER_NAME_LONG_ID = TAG = ".id";
    private String USER_PROFILE_THUMBNAIL = TAG = ".userProfileThumbnail";
    private String USER_IS_UPLOADED_BEFORE_TAG = TAG = ".isUploadedBefore";
    private SharedPreferences pref;


    public DiskCache(SharedPreferences preferences) {
        this.pref = preferences;
    }

    public boolean setUserToken(String token) {
        return this.pref.edit().putString(USER_TOKEN_TAG,  token).commit();
    }

    public boolean setUserData(String name, long id, String thumbnailUrl) {
        return this.pref.edit()
                .putString(USER_NAME_TAG, name)
                .putLong(USER_NAME_LONG_ID, id)
                .putString(USER_PROFILE_THUMBNAIL, thumbnailUrl)
                .commit();
    }

    public boolean clearUserData() {
        return this.pref.edit()
                .remove(USER_TOKEN_TAG)
                .remove(USER_NAME_TAG)
                .remove(USER_NAME_LONG_ID)
                .remove(USER_PROFILE_THUMBNAIL)
                .remove(USER_IS_UPLOADED_BEFORE_TAG)
                .commit();
    }

    public boolean isUploadedPromiseBefore() {
        return this.pref.getBoolean(USER_IS_UPLOADED_BEFORE_TAG, false);
    }

    public boolean setUploadedPromiseBefore(boolean isUploadedPromise) {
        return this.pref.edit()
                .putBoolean(USER_IS_UPLOADED_BEFORE_TAG, isUploadedPromise)
                .commit();
    }


    @NonNull
    public long getUserId() {
        return this.pref.getLong(USER_NAME_LONG_ID, -1L);
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
    public String getProfileThumbnail() {
        return this.pref.getString(USER_PROFILE_THUMBNAIL, "");
    }


}
