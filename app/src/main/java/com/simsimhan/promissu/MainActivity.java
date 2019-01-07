package com.simsimhan.promissu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.simsimhan.promissu.util.NavigationUtil;
import com.simsimhan.promissu.view.DummyTutorialActivity;

import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements TabLayout.BaseOnTabSelectedListener {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "MainActivity";
    private FrameLayout frameView;
//    private FragmentManager fragmentManager;
    private Animation fabOpen, fabClose;
    private DrawerLayout drawerLayout;
    private ImageView profileImage;
    private TextView userName;
    private ImageView profileMainImage;
    private TextView mainText;
    private MainFragmentPagerAdapter adapterViewPager;
    private TabLayout tabLayout;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (TextUtils.isEmpty(PromissuApplication.getDiskCache().getUserToken()) && TextUtils.isEmpty(PromissuApplication.getDiskCache().getUserName())) {
            NavigationUtil.replaceWithLoginView(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        ViewPager vpPager = findViewById(R.id.vpPager);
        mainText = findViewById(R.id.main_intro);
        floatingActionButton = findViewById(R.id.floating_action_button);

        floatingActionButton.setOnClickListener(v -> {
            // do something here
            NavigationUtil.openAddPromiseScreen(MainActivity.this);
        });

        profileMainImage = findViewById(R.id.profile_image_main);
        adapterViewPager = new MainFragmentPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);
        tabLayout.addOnTabSelectedListener(this);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getCustomTabView(i));
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);

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

        Glide.with(this)
                .load(PromissuApplication.getDiskCache().getProfileThumbnail())
                .apply(RequestOptions.circleCropTransform())
                .into(profileMainImage);

        String userNameText = PromissuApplication.getDiskCache().getUserName();
        mainText.setText(Html.fromHtml(getString(R.string.main_dummy, userNameText)));
        userName.setText(userNameText);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(userNameText);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                Toast.makeText(MainActivity.this, "Selected page position: " + position, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }

//    public void anim() {
//        if (floatingActionButton.isExpanded()) {
//            floatingActionButton.startAnimation(fabClose);
//            floatingActionButton.setClickable(false);
//        } else {
//            floatingActionButton.startAnimation(fabOpen);
//            floatingActionButton.setClickable(true);
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabLayout.removeOnTabSelectedListener(this);
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


    public View getCustomTabView(int index) {
        View customView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_custom_tab, null);
        TextView text = customView.findViewById(R.id.custom_tab_textView);
        ConstraintLayout container = customView.findViewById(R.id.container);

        if (index == 0) {
            text.setText("약속");
            text.setTextColor(ContextCompat.getColor(this, R.color.white));
            container.setBackground(ContextCompat.getDrawable(this, R.drawable.round_background));
        } else {
            text.setText("지난 약속");
            text.setTextColor(ContextCompat.getColor(this, R.color.grey_02));
            container.setBackground(ContextCompat.getDrawable(this, R.drawable.round_red_background));
        }

        return customView;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view != null) {
            TextView text = view.findViewById(R.id.custom_tab_textView);
            text.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        if (view != null) {
            TextView text = view.findViewById(R.id.custom_tab_textView);
            text.setTextColor(ContextCompat.getColor(this, R.color.grey_02));
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}