package com.simsimhan.promissu.promise;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.model.Promise;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PendingPromiseActivity extends AppCompatActivity {
    private CompositeDisposable disposables;
    private DrawerLayout drawerLayout;
    private AppCompatButton inviteButton;
    private Promise.Response promise;
    private DateTime promiseDelayEndTime;
    private DateTime promiseCreatedTime;

    private TextView promiseLeftTimeTextView;
    private TextView promiseDateActual;
    private TextView locationTextView;
    private DateTime now;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_promise);
        drawerLayout = findViewById(R.id.drawer_layout);
        promiseLeftTimeTextView = findViewById(R.id.timer);
        inviteButton = findViewById(R.id.invite_people);
        promiseDateActual = findViewById(R.id.promise_date_actual);
        locationTextView = findViewById(R.id.promise_location_actual);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // swap UI fragments here
                    switch (menuItem.getItemId()) {
                        default:
                            return false;
                    }
                });

        promise = getIntent().getParcelableExtra("promise");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        TextView title = toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        title.setText(promise.getTitle());
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        }

        disposables = new CompositeDisposable();
        changeStatusBarColor();

        promiseCreatedTime = new DateTime(promise.getCreatedAt());
        promiseDelayEndTime = promiseCreatedTime.plusMinutes(promise.getWaitTime());
        now = new DateTime();

        DateTime promiseStartTime = new DateTime(promise.getStartTime());
        Timber.d("onCreate(): %s", promiseStartTime.toString());
        promiseDateActual.setText(promiseStartTime.toString("MM/dd/yyyy h:mm"));
        // TODO: get promise location and set text for locationTextView

        String adminId = promise.getAdmin_id();

        if (adminId != null && !adminId.isEmpty()) {
            long roomAdminId = Long.valueOf(adminId);
            long localUserId = PromissuApplication.getDiskCache().getUserId();

            inviteButton.setVisibility(roomAdminId == localUserId ? View.VISIBLE : View.GONE);
        } else {
            throw new IllegalStateException("Admin ID should always be visible");
        }

        startTimer();
        inviteButton.setOnClickListener(v -> {
            DateTime promiseDate = new DateTime(promise.getStartTime());

            LocationTemplate params = LocationTemplate.newBuilder(promise.getLocation() +" 좌표: (" + promise.getLocation_lan() + ", " + promise.getLocation_lon() + ")",
                    ContentObject.newBuilder(promise.getTitle(),
                            "https://i.pinimg.com/originals/92/e4/43/92e443862a7ae5db7cf74b41db2f5e37.jpg",
                            LinkObject.newBuilder()
                                    .setWebUrl("https://developers.kakao.com")
                                    .setMobileWebUrl("https://developers.kakao.com")
                                    .build())
                            .setDescrption(
                                    promiseDate.getYear() + "년 "
                                    + promiseDate.getMonthOfYear()  + "월 "
                                    + promiseDate.getDayOfMonth() + "일 "
                                    + promise.getDescription())
                            .build())
                    .setAddressTitle(promise.getLocation() + " 좌표: (" + promise.getLocation_lan() + ", " + promise.getLocation_lon() + ")")
                    .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                            .setWebUrl("'https://developers.kakao.com")
                            .setMobileWebUrl("'https://developers.kakao.com")
                            .setAndroidExecutionParams("roomID=" + promise.getId())
                            .setIosExecutionParams("roomID=" + promise.getId())
                            .build()))
                    .build();

            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
            serverCallbackArgs.put("user_id", "${current_user_id}");
            serverCallbackArgs.put("product_id", "${shared_product_id}");

            KakaoLinkService.getInstance().sendDefault(PendingPromiseActivity.this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Timber.e(errorResult.getException());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    Timber.d("onSuccess(): " + result.toString());
                }
            });



//                This is kakao friends service
//                AppFriendContext friendContext = new AppFriendContext(true, 0, 100, "asc");
//
//                KakaoTalkService.getInstance().requestAppFriends(friendContext, new TalkResponseCallback<AppFriendsResponse>() {
//                    @Override
//                    public void onSessionClosed(ErrorResult errorResult) {
//                        Timber.e(errorResult.getException());
//                        NavigationUtil.replaceWithLoginView(PendingPromiseActivity.this);
//                    }
//
//                    @Override
//                    public void onNotSignedUp() {
//                        NavigationUtil.replaceWithLoginView(PendingPromiseActivity.this);
//                    }
//
//                    @Override
//                    public void onSuccess(AppFriendsResponse result) {
//                        if (result.getFriends() == null) {
//                            Timber.d("onSuccess(): null");
//                            return;
//                        }
//
//                        Timber.d("onSuccess(): %s", result.getFriends().size());
//
//                        for (int i = 0; i < result.getFriends().size(); i++) {
//                            Timber.d("onSuccess(): %s", result.getFriends().get(i).getProfileNickname());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(ErrorResult errorResult) {
//                        super.onFailure(errorResult);
//                        Timber.e(errorResult.getException());
//                    }
//
//                    @Override
//                    public void onNotKakaoTalkUser() {
//                        Timber.d("onNotKakaoTalkUser()");
//                    }
//
//                    @Override
//                    public void onFailureForUiThread(ErrorResult errorResult) {
//                        super.onFailureForUiThread(errorResult);
//                        Timber.e(errorResult.getException());
//                    }
//                });


//                disposables.add(
//                        PromissuApplication.getRetrofit()
//                                .create(AuthAPI.class)
//                                .inviteFriends("Bearer " + PromissuApplication.getDiskCache().getUserToken(), request)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(onNext -> {
//                                    Timber.d("" + onNext.toString());
//                                    Toast.makeText(getBaseContext(), "약속 생성이 완료 되었습니다.", Toast.LENGTH_LONG).show();
//                                    // TODO: do something here
//
//                                    setResult(RESULT_OK);
//
//                                    NavigationUtil.openPendingScreen(CreatePromiseActivity.this, onNext);
//                                    finish();
//                                }, onError -> {
//                                    Timber.e(onError);
//                                }));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void startTimer() {
        disposables.add(
                Observable.interval(0, 1, TimeUnit.SECONDS)
                        .map(timer -> now.plusSeconds(timer.intValue()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            Period period = new Period(onNext, promiseDelayEndTime);
                            promiseLeftTimeTextView.setText(addPaddingIfSingleDigit(period.getHours()) + ":" +
                                    addPaddingIfSingleDigit(period.getMinutes()) + ":" +
                                    addPaddingIfSingleDigit(period.getSeconds()));
                        }, onError -> {

                        }));
    }

    private String addPaddingIfSingleDigit(int time) {
        if (time >= 0) {
            if (time < 10) {
                return "0" + time;
            } else {
                return "" + time;
            }
        } else {
            if (time < -10) {
                return "-0" + (time * -1);
            } else {
                return "" + time;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pending_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_members:
                drawerLayout.openDrawer(GravityCompat.END);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeStatusBarColor() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.main_01));
        }
    }


}
