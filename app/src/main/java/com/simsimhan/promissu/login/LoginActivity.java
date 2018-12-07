package com.simsimhan.promissu.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kakao.auth.AuthType;
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
import com.simsimhan.promissu.util.NavigationUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;
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

        Session.getCurrentSession().addCallback(this);
        Session.getCurrentSession().checkAndImplicitOpen();
        loginButton = findViewById(R.id.btn_kakao_login);
//        StringUtil.getHashKey(this);  Kakao key hash
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(this);
        disposables.clear();
    }

    @Override
    public void onSessionOpened() {
        UserManagement.getInstance().me(new MeV2ResponseCallback() {
            @Override
            public void onSuccess(MeV2Response result) {
                // TODO: go to main screen
                PromissuApplication.getDiskCache().setUserData(result.getNickname(), result.getId(), result.getThumbnailImagePath());
                NavigationUtil.replaceWithMainView(LoginActivity.this);
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
