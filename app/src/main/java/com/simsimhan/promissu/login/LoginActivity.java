package com.simsimhan.promissu.login;

import android.os.Bundle;
import android.view.View;

import com.simsimhan.promissu.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private CompositeDisposable disposables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        disposables = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }


    @Override
    public void onClick(View v) {
//        if (emailEditText == null || passwordEditText == null || TextUtils.isEmpty(emailEditText.getText()) || TextUtils.isEmpty(passwordEditText.getText())) {
//            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        disposables.add(
//                PromissuApplication.getRetrofit()
//                        .create(AuthAPI.class)
//                        .login(new Login.Request(
//                                emailEditText.getText().toString(),
//                                passwordEditText.getText().toString()))
//                        .doOnNext(next -> PromissuApplication.getDiskCache().setUserToken(next.getToken()))
//                        .doOnNext(next -> PromissuApplication.getDiskCache().setUserData(next.getUser().getName(), next.getUser().getEmail()))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(onNext -> {
//                            // save token
//                            NavigationUtil.replaceWithMainView(LoginActivity.this);
//                        }, onError -> {
//                            Toast.makeText(this, "로그인에 실패하셨습니다. 비밀번호와 아이디를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
//
//                            Timber.e(onError);
//                            passwordEditText.setError(onError.getMessage());
//                        })
//        );
    }
}
