package com.simsimhan.promissu.promise.create;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.view.DummyTutorialActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class CreatePromiseActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promise);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.vpPager);


        if (!PromissuApplication.getDiskCache().isUploadedPromiseBefore()) {
            Intent intent = new Intent(this, DummyTutorialActivity.class);
            startActivity(intent);
        }

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
}
