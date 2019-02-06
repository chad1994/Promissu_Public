package com.simsimhan.promissu.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.simsimhan.promissu.BuildConfig;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.map.search.DaumAPI;
import com.simsimhan.promissu.map.search.FullListAdapter;
import com.simsimhan.promissu.map.search.Item;
import com.simsimhan.promissu.util.NavigationUtil;
import com.simsimhan.promissu.util.ScreenUtil;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdate;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MapSearchActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.MapViewEventListener {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PromiseDetailActivity";

    private Toolbar toolbar;
    private MaterialSearchView searchView;
    private FloatingActionButton actionButton;
    private MapView mapView;
    private CompositeDisposable disposables;
    private FullListAdapter suggestionAdapter;
    private String selectedPromiseLocationName = "";
    private String selectedPromiseLocationAddress = "";
    private double x;
    private double y;
    private boolean locationPermissionGranted;

    // location
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private MapPOIItem myMarker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        disposables = new CompositeDisposable();
        actionButton = findViewById(R.id.floating_action_button);
        myMarker = new MapPOIItem();
        myMarker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        myMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ViewGroup mapViewContainer = findViewById(R.id.container);

        suggestionAdapter = new FullListAdapter(new ArrayList<>(), item -> {
            if (searchView == null || mapView == null || item == null) return;

            searchView.dismissSuggestions();

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(item.getPlace_name());
            marker.setTag(item.getId());
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(item.getY(), item.getX()));
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

            selectedPromiseLocationName = item.getPlace_name();
            selectedPromiseLocationAddress = item.getAddress_name();
            x = item.getX();
            y = item.getY();

            mapView.addPOIItem(marker);
            mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(item.getY(), item.getX())));
        });

        searchView = findViewById(R.id.search_view);
        searchView.setAdapter(suggestionAdapter);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideSoftKeyboard();

                MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude;
                double longitude = geoCoordinate.longitude;
//                int radius = 10000;
//                int page = 1;
                mapView.removeAllPOIItems();

                disposables.add(
                        PromissuApplication.getDaumRetrofit()
                                .create(DaumAPI.class)
                                .searchMapWithKeyword(getString(R.string.rest_key), latitude, longitude, query)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(onNext -> {
                                    if (onNext != null && onNext.getDocuments() != null) {
                                        suggestionAdapter.replaceAll(onNext.getDocuments());
                                        searchView.showSuggestions();
                                    } else {
                                        Toast.makeText(MapSearchActivity.this, "결과가 없습니다. 다시 검색해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }, onError -> {
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(MapSearchActivity.this, "[DEV] onQueryTextSubmit() check log", Toast.LENGTH_SHORT).show();
                                    }

                                    Timber.e("onQueryTextSubmit(): %s", onError.toString());
                                }));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                if (suggestionAdapter.getCount() > 0) {
                    suggestionAdapter.replaceAll(new ArrayList<>());
                    return true;
                }

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
                suggestionAdapter.replaceAll(new ArrayList<>());
                mapView.removeAllPOIItems();

            }
        });
        toolbar = findViewById(R.id.toolbar);

        mapView = new MapView(this);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
        mapViewContainer.addView(mapView);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void hideSoftKeyboard() {
        ScreenUtil.closeKeyboard(this.getCurrentFocus(), (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
        if (!TextUtils.isEmpty(selectedPromiseLocationAddress) && !TextUtils.isEmpty(selectedPromiseLocationName)) {
            Intent data = new Intent();
            data.putExtra("selected_name", selectedPromiseLocationName);
            data.putExtra("selected_address", selectedPromiseLocationAddress);
            data.putExtra("selected_x", x);
            data.putExtra("selected_y", y);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Timber.e("onPOIItemSelected(): mapPOIItem " + mapPOIItem.getItemName());
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case NavigationUtil.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }

        updateLocationUI();
    }

    private void updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                getDeviceLocation();
            } else {
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = (Location) task.getResult();

                            mapView.removePOIItem(myMarker);
                            myMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));

                            mapView.addPOIItem(myMarker);
                            mapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));
                        } else {
                            Timber.d("Current location is null. Using defaults.");
                            Timber.e(task.getException());

                            //  move to default location

                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Timber.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, NavigationUtil.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
//            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.ic_launcher);
//            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
//            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText("Custom CalloutBalloon");
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }
}
