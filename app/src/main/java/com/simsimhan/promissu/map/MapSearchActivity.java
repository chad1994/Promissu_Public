package com.simsimhan.promissu.map;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.simsimhan.promissu.BuildConfig;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.map.search.DaumAPI;
import com.simsimhan.promissu.map.search.Item;
import com.simsimhan.promissu.util.ScreenUtil;

import net.daum.mf.map.api.CameraUpdate;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MapSearchActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PromiseDetailActivity";

    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private MapView mapView;
    private CompositeDisposable disposables;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        disposables = new CompositeDisposable();

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.container);

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                hideSoftKeyboard();

                MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude;
                double longitude = geoCoordinate.longitude;
//                int radius = 10000;
                int page = 1;

                disposables.add(
                        PromissuApplication.getDaumRetrofit()
                                .create(DaumAPI.class)
                                .searchMapWithKeyword(getString(R.string.rest_key), latitude, longitude, query)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(onNext -> {
                                    Item firstResult = null;
                                    if (onNext != null && onNext.getDocuments() != null) {
                                        for (int i = 0; i < onNext.getDocuments().size(); i++) {
                                            Item item = onNext.getDocuments().get(i);

                                            if (i == 0) {
                                                firstResult = item;
                                            }

                                            Timber.d(item.toString());

                                            MapPOIItem marker = new MapPOIItem();
                                            marker.setItemName(item.getPlace_name());
                                            marker.setTag(item.getId());
                                            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(item.getY(), item.getX()));
                                            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                                            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                                            mapView.addPOIItem(marker);
                                        }
                                    }

//                                    if (firstResult != null) {
//                                        mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(firstResult.getY(), firstResult.getX())));
//                                    }
                                }, onError -> {
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(MapSearchActivity.this, "[DEV] onQueryTextSubmit() check log", Toast.LENGTH_SHORT).show();
                                    }

                                    Timber.e("onQueryTextSubmit(): %s", onError.toString());
                                }));

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        toolbar = findViewById(R.id.toolbar);

        mapView = new MapView(this);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapViewContainer.addView(mapView);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void hideSoftKeyboard() {
        ScreenUtil.closeKeyboard(this.getCurrentFocus(), (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
