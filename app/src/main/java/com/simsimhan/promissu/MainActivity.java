package com.simsimhan.promissu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.simsimhan.promissu.promise.PromiseFragment;
import com.simsimhan.promissu.util.NavigationUtil;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONObject;

import java.time.Instant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity  {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "MainActivity";
    private FrameLayout frameView;
    private FragmentManager fragmentManager;
    private DrawerLayout drawerLayout;
    private ImageView profileImage;
    private TextView userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (TextUtils.isEmpty(PromissuApplication.getDiskCache().getUserToken()) && TextUtils.isEmpty(PromissuApplication.getDiskCache().getUserName())) {
            NavigationUtil.replaceWithLoginView(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setTitle("");
        }

        changeStatusBarColor();
        frameView = findViewById(R.id.frame);
        drawerLayout = findViewById(R.id.drawer_layout);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    // swap UI fragments here

                    return true;
                });
        profileImage = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        userName = navigationView.getHeaderView(0).findViewById(R.id.profile_username);

        Glide.with(this)
                .load(PromissuApplication.getDiskCache().getProfileThumbnail())
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage);

        userName.setText(PromissuApplication.getDiskCache().getUserName());

        Fragment cachedFragment = fragmentManager.findFragmentByTag(PromiseFragment.TAG);
        if (cachedFragment == null) {
            cachedFragment = PromiseFragment.newInstance();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.frame, cachedFragment)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                    Timber.i(referringParams.toString());
                } else {
                    Timber.i(error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

}