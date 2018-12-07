package com.simsimhan.promissu.promise;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simsimhan.promissu.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class PromiseFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "PromiseFragment";
    private CompositeDisposable disposables;
    private String token;
    private SwipeRefreshLayout swipeContainer;
    private PromiseAdapter adapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    public static PromiseFragment newInstance() {
        PromiseFragment fragment = new PromiseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // get arguments and set here
        }

        adapter = new PromiseAdapter((AppCompatActivity) getActivity(), new ArrayList<>());
        disposables = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promise_list, container, false);

        Activity currentActivity = getActivity();
        if (currentActivity instanceof AppCompatActivity) {
            toolbar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity) currentActivity).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) currentActivity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setTitle("");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
        }

        recyclerView = view.findViewById(R.id.promise_recycler_view);
        swipeContainer = view.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark);
        swipeContainer.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(R.dimen.refresher_start));
        swipeContainer.post(() -> {
            swipeContainer.setRefreshing(true);
            fetch(false);
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

    private void fetch(boolean shouldIndicateNew) {
//        disposables.add(
//                api.getMyPrescriptions(token)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(onNext -> {
//                            swipeContainer.setRefreshing(false);
//                            adapter.reset(onNext, shouldIndicateNew);
//                        }, onError -> {
//                            swipeContainer.setRefreshing(false);
//                            NavigationUtil.replaceWithLoginView((AppCompatActivity) getActivity());
//
//                            Timber.e(onError);
//                            if (DEBUG)
//                                Toast.makeText(getContext(), "[DEV] " + onError.getMessage(), Toast.LENGTH_SHORT).show();
//                        }));
    }

    @Override
    public void onRefresh() {
        fetch(false);
    }
}
