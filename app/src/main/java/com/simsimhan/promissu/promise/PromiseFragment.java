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

import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.AuthAPI;
import com.simsimhan.promissu.network.model.Promise;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private boolean isPastPromise;

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

        adapter = new PromiseAdapter((AppCompatActivity) getActivity(), new ArrayList<>(), isPastPromise);
        disposables = new CompositeDisposable();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promise_list, container, false);

        recyclerView = view.findViewById(R.id.promise_recycler_view);
        swipeContainer = view.findViewById(R.id.swipe_container);
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
//                PromissuApplication.getRetrofit()
//                        .create(AuthAPI.class)
//                        .getMyPromise(token, 0, 9)
                getDummyData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            swipeContainer.setRefreshing(false);
                            adapter.reset(onNext);
                        }, onError -> {
                            swipeContainer.setRefreshing(false);
                            Timber.e(onError);
                        }));
    }

    private Observable<List<Promise.Response>> getDummyData() {
        List<Promise.Response> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(new Promise.Response(DateTime.now().plusDays((int) (Math.random() * 20)).toDate(), "asjdiof " + i));
        }

        return Observable.just(list);

    }

    @Override
    public void onRefresh() {
        fetch();
    }
}
