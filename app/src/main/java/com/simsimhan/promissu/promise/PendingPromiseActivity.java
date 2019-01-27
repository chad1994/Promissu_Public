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
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.simsimhan.promissu.MainActivity;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.model.Promise;
import com.simsimhan.promissu.util.DialogUtil;
import com.simsimhan.promissu.util.NavigationUtil;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;

import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class PendingPromiseActivity extends AppCompatActivity {
    private CompositeDisposable disposables;
    private DrawerLayout drawerLayout;
    private Promise.Response promise;
    private DateTime promiseStartTime;
    private TextView promiseLeftTimeTextView;
    private DateTime now;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_promise);
        drawerLayout = findViewById(R.id.drawer_layout);
        promiseLeftTimeTextView = findViewById(R.id.timer);

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

        DateTime createdTime = new DateTime(promise.getCreatedAt());
        promiseStartTime = createdTime.plusMinutes(promise.getWaitTime());
        now = new DateTime();

        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void startTimer() {
        disposables.add(
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(timer -> promiseStartTime.plusSeconds(timer.intValue()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext -> {
//                    DateTime difference = now.minus(onNext);
//                    Hours hoursBetween = Hours.hoursBetween(onNext, now);
//                    Minutes minutesBetween = Minutes.minutesBetween(onNext, now);
//                    Seconds secondsBetween = Seconds.secondsBetween(onNext, now);
                    Period period = new Period(onNext, now);
                    promiseLeftTimeTextView.setText(period.getHours() + ":" + period.getMinutes() + ":" + period.getSeconds());
                }, onError -> {

                }));
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
