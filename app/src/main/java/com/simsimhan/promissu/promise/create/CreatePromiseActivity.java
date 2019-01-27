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
        String title = "";
        if (firstFragment != null) {
            title = firstFragment.getTitle();
        }

        if (secondFragment != null) {
            
        }
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
