package com.simsimhan.promissu.promise.create;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.AuthAPI;
import com.simsimhan.promissu.network.model.Promise;

import org.joda.time.DateTime;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.simsimhan.promissu.util.NavigationUtil.REQUEST_MAP_SEARCH;

public class CreatePromiseActivity extends AppCompatActivity {
    private static final int NUM_ITEMS = 3;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private CreatePromiseFragmentPagerAdapter adapterViewPager;
    private CreatePromiseFragment firstFragment;
    private CreatePromiseFragment secondFragment;
    private CreatePromiseFragment thirdFragment;
    private String token;
    private CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promise);

        if (!PromissuApplication.getDiskCache().isUploadedPromiseBefore()) {
//            Intent intent = new Intent(this, DummyTutorialActivity.class);
//            startActivity(intent);
            Toast.makeText(this, "좌우로 미세요.", Toast.LENGTH_LONG).show();
        }

        adapterViewPager = new CreatePromiseFragmentPagerAdapter(getSupportFragmentManager());
        token = PromissuApplication.getDiskCache().getUserToken();
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.vpPager);
        viewPager.setAdapter(adapterViewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));

        setSupportActionBar(toolbar);
        changeStatusBarColor();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void changeStatusBarColor() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));
        }
    }

    public void createPromise() {
//        {
//                "title": "테스트 모임333",
//                "description": "우리 함께 모여서 놀아요",
//                "start_datetime": "2018-01-28 14:01",
//                "end_datetime": "2018-01-28 15:00",
//                "waiting_time": 1440,
//                "location_x": "37.499385",
//                "location_y": "127.029204"
//        }
        String title = "";
        String detail = "";
        Date startTime = null;
        Date endTime = null;
        int waitingTime = 30;
        float locationX = 0f;
        float locationY = 0f;

        if (firstFragment != null) {
            title = firstFragment.getTitle();
            detail = title;

            if (title == null || title.isEmpty()) {
                Toast.makeText(this, "제목을 설정해주세요!", Toast.LENGTH_LONG).show();
                viewPager.setCurrentItem(0, true);
                return;
            }
        }

        if (secondFragment != null) {
            DateTime selectedTime = secondFragment.getStartTime();
            double x = secondFragment.getLocationX();
            double y = secondFragment.getLocationX();

            if (x == 0 && y == 0) {
                Toast.makeText(this, "위치를 설정해주세요!", Toast.LENGTH_LONG).show();
                viewPager.setCurrentItem(1, true);
                return;
            }

            if (selectedTime == null) {
                Toast.makeText(this, "시간을 설정해주세요!", Toast.LENGTH_LONG).show();
                viewPager.setCurrentItem(1, true);
                return;
            }

            startTime = selectedTime.toDate();
            endTime = selectedTime.plusMinutes(30).toDate();
            locationX = (float) x;
            locationY = (float) y;
        }

        if (thirdFragment != null) {
            waitingTime = thirdFragment.getWaitTime();

            if (waitingTime == 0) {
                Toast.makeText(this, "대기 시간을 설정해주세요!", Toast.LENGTH_LONG).show();
                viewPager.setCurrentItem(2, true);
                return;
            }
        }

        remoteCallCreatePromise(new Promise.Request(title, detail, startTime, endTime, locationX, locationY, waitingTime));
    }

    private void remoteCallCreatePromise(Promise.Request request) {
        disposables.add(
                PromissuApplication.getRetrofit()
                        .create(AuthAPI.class)
                        .createPromise("Bearer " + token, request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            Timber.d("" + onNext.toString());
                            Toast.makeText(getBaseContext(), "약속 생성이 완료 되었습니다.", Toast.LENGTH_LONG).show();
                            // TODO: do something here

                            setResult(RESULT_OK);
                            finish();
                        }, onError -> {
                            Timber.e(onError);
                        }));
    }


    public class CreatePromiseFragmentPagerAdapter extends FragmentPagerAdapter {
        CreatePromiseFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        @Nullable
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                case 1:
                case 2:
                    return CreatePromiseFragment.newInstance(position, (String) getPageTitle(position));
                default:
                    return null;
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

            switch (position) {
                case 0:
                    firstFragment = (CreatePromiseFragment) createdFragment;
                    break;
                case 1:
                    secondFragment = (CreatePromiseFragment) createdFragment;
                    break;
                case 2:
                    thirdFragment = (CreatePromiseFragment) createdFragment;
                    break;
            }

            return createdFragment;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return (position + 1) + " 단계";
        }
    }

}
