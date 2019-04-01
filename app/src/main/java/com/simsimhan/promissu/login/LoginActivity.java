package com.simsimhan.promissu.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;
import com.simsimhan.promissu.BuildConfig;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.AuthAPI;
import com.simsimhan.promissu.network.Login;
import com.simsimhan.promissu.util.NavigationUtil;
import com.simsimhan.promissu.util.StringUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity implements ISessionCallback {
    private CompositeDisposable disposables;
    private LoginButton loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        disposables = new CompositeDisposable();
        loginButton = findViewById(R.id.btn_kakao_login);
//        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);

//        Kakao key hash for debug
        StringUtil.getHashKey(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Session.getCurrentSession().addCallback(LoginActivity.this);
//        Session.getCurrentSession().checkAndImplicitOpen();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Session.getCurrentSession().removeCallback(this);
        disposables.clear();
    }

    @Override
    public void onSessionOpened() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSuccess(MeV2Response result) {
                PromissuApplication.getDiskCache().setUserData(result.getNickname(), result.getId(), result.getThumbnailImagePath());
                String userSessionToken = Session.getCurrentSession().getTokenInfo().getAccessToken();
                if (BuildConfig.DEBUG) {
                    Toast.makeText(LoginActivity.this, "[DEV] onSuccess() user token: " + userSessionToken, Toast.LENGTH_SHORT).show();
                }

                disposables.add(
                        PromissuApplication.getRetrofit()
                                .create(AuthAPI.class)
                                .loginKakao(new Login.Request(userSessionToken))
                                .doOnNext(next -> {
                                    PromissuApplication.getDiskCache().setUserData(result.getNickname(), result.getId(), result.getThumbnailImagePath());
                                    PromissuApplication.getDiskCache().setUserToken(next.getToken());
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(onNext -> {
                                    // save token
                                    NavigationUtil.replaceWithMainView(LoginActivity.this);
                                }, onError -> {
                                    Timber.e("onSessionClosed(): %s", onError.toString());
                                    Toast.makeText(LoginActivity.this, "서버 점검 중입니다. 나중에 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }));
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                if (BuildConfig.DEBUG) {
                    Toast.makeText(LoginActivity.this, "[DEV] onSessionClosed() check log", Toast.LENGTH_SHORT).show();
                }

                Timber.e("onSessionClosed(): %s", errorResult.toString());
            }


        });
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(this, "[DEV] onSessionOpenFailed() check log", Toast.LENGTH_SHORT).show();
        }

        Timber.e("onSessionOpenFailed(): %s", exception.toString());
    }
}
