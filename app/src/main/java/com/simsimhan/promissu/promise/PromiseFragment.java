package com.simsimhan.promissu.promise;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.simsimhan.promissu.MainActivity;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.AuthAPI;
import com.simsimhan.promissu.network.model.Promise;
import com.simsimhan.promissu.util.NavigationUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PromiseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "PromiseFragment";
    private CompositeDisposable disposables;
    private String token;
    private SwipeRefreshLayout swipeContainer;
    private PromiseAdapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ImageView emptyImageView;
    private boolean isPastPromise;
    private ConstraintLayout emptyHolder;

    public static PromiseFragment newInstance(int position, String title, boolean isPastPromise) {
        PromiseFragment fragment = new PromiseFragment();
        Bundle args = new Bundle();
        args.putInt("Page_key", position);
        args.putString("Title_key", title);
        args.putBoolean("is_past_key", isPastPromise);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // get arguments and set here
            isPastPromise = getArguments().getBoolean("is_past_key");
        }

        token = PromissuApplication.Companion.getDiskCache().getUserToken();
        adapter = new PromiseAdapter((AppCompatActivity) getActivity(), new ArrayList<>(), isPastPromise);
        disposables = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promise_list, container, false);

        recyclerView = view.findViewById(R.id.promise_recycler_view);
        swipeContainer = view.findViewById(R.id.swipe_container);
        emptyHolder = view.findViewById(R.id.empty_view_holder);
        emptyImageView = view.findViewById(R.id.empty_view_image);

        emptyHolder.setVisibility(View.GONE);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipeContainer.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(R.dimen.refresher_start));
        swipeContainer.post(() -> {
            swipeContainer.setRefreshing(true);
            fetch();
        });

        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (toolbar != null && recyclerView != null) {
                    toolbar.setSelected(recyclerView.canScrollVertically(-1));
                }
            });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult(): " + requestCode + " " + resultCode);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflate menuinflator if we need action from options
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void fetch() {
        disposables.add(
                PromissuApplication.Companion.getRetrofit()
                        .create(AuthAPI.class)
                        .getMyPromise("Bearer " + token, 0, 9, isPastPromise ? "past" : "future")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            swipeContainer.setRefreshing(false);
                            adapter.reset(onNext);
                            setEmptyViewVisible(onNext.size() <= 0);
                        }, onError -> {
                            swipeContainer.setRefreshing(false);
                            Timber.e(onError);
                            Toast.makeText(getActivity(), "서버 점검 중입니다. 다시 로그인해 주세요.", Toast.LENGTH_SHORT).show();
                            PromissuApplication.Companion.getDiskCache().clearUserData();
                            NavigationUtil.replaceWithLoginView((AppCompatActivity) getActivity());
                        }));
    }

    private void setEmptyViewVisible(boolean setVisible) {
        if (emptyHolder != null && recyclerView != null && emptyImageView != null) {
//            if (setVisible) recyclerView.scrollTo(0, 0);
            emptyHolder.setVisibility(setVisible ? View.VISIBLE : View.INVISIBLE);
            recyclerView.setBackgroundColor(ContextCompat.getColor(recyclerView.getContext(), isPastPromise ? R.color.past_background_color : R.color.background_grey));
            emptyImageView.setImageDrawable(ContextCompat.getDrawable(emptyImageView.getContext(), isPastPromise ? R.drawable.no_appointment_red : R.drawable.no_appointment_blue));
            emptyHolder.setBackgroundColor(ContextCompat.getColor(emptyHolder.getContext(), isPastPromise ? R.color.past_background_color : R.color.background_grey));
        }
    }

    @Override
    public void onRefresh() {
        fetch();
    }
}
