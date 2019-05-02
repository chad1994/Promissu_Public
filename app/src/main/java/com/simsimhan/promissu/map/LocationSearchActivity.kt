package com.simsimhan.promissu.map

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityMapSearchBinding
import com.simsimhan.promissu.detail.PromiseDetailActivity
import com.simsimhan.promissu.map.search.FullListAdapter
import com.simsimhan.promissu.model.LocationSearchItem
import com.simsimhan.promissu.network.NaverAPI
import com.simsimhan.promissu.promise.create.CreateViewModel
import com.simsimhan.promissu.util.keyboardHide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class LocationSearchActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    companion object {
        //        private const val DETAIL_CONTAINER_ID = R.id.detail_activity_container
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var binding: ActivityMapSearchBinding
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var searchMarker: Marker
    private lateinit var searchInfo: InfoWindow
    private lateinit var searchView: MaterialSearchView
    private lateinit var toolbar: Toolbar
    private lateinit var suggestionAdapter: FullListAdapter
    private var location: String? = null
    private var locationName: String? = null
    private var x: Double? = null
    private var y: Double? = null
    private val viewModel: CreateViewModel by viewModel()
    private lateinit var locationSource: FusedLocationSource
    private var locationFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_map_search)
//        viewModel = ViewModelProviders.of(this).get(CreateViewModel::class.java)

        if (savedInstanceState == null) {
            mapView = binding.container
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        searchMarker = Marker()
        searchInfo = InfoWindow()
        binding.apply {
            lifecycleOwner = this@LocationSearchActivity
            viewModel = this@LocationSearchActivity.viewModel
        }
        toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        searchView = binding.searchView
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
            }

            override fun onSearchViewClosed() {

            }
        })
    }

    override fun onMapReady(naverMap: NaverMap) {
        setSearchAdapter(naverMap)
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        searchView.setAdapter(suggestionAdapter)
        initMyLocation(naverMap)
        setQueryListener(naverMap)
        setOnSearchListener()
    }

    private fun initMyLocation(naverMap: NaverMap) {
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.icon = OverlayImage.fromResource(R.drawable.ic_icon_mylocation_overlay)
        locationOverlay.circleColor = Color.parseColor("#48ef006d")

        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.addOnLocationChangeListener {
            if(!locationFlag){
                val cameraUpdate = CameraUpdate.scrollAndZoomTo(locationOverlay.position, 16.0)
                naverMap.moveCamera(cameraUpdate)
                locationFlag = true
            }
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "사용을 위해 설정 -> 애플리케이션에서 권한 속성에서 위치 권한에 동의 해주세요.", Toast.LENGTH_LONG).show()
                finish()
                return
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setSearchAdapter(naverMap: NaverMap) {
        suggestionAdapter = FullListAdapter(ArrayList<LocationSearchItem>()) { item ->
            if (searchView == null || mapView == null || item == null) return@FullListAdapter

            searchView.dismissSuggestions()
            searchMarker.captionText = item.name
            searchMarker.position = LatLng(item.y.toDouble(), item.x.toDouble())
            searchMarker.map = naverMap
            searchInfo.open(searchMarker)
            searchInfo.tag = "info"
            searchInfo.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return "위치지정완료 >"
                }
            }
            searchInfo.onClickListener = this

            val cameraUpdate = CameraUpdate.scrollAndZoomTo(searchMarker.position, 16.0)
            naverMap.moveCamera(cameraUpdate)

            x = item.x.toDouble()
            y = item.y.toDouble()
            locationName = item.name
            location = item.jibun_address

        }
    }

    private fun setQueryListener(naverMap: NaverMap) {
        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                keyboardHide()

                searchMarker.map = null
                searchInfo.map = null
                viewModel.addDisposable(
                        PromissuApplication.naverRetrofit!!
                                .create(NaverAPI::class.java)
                                .searchLocationWithKeyword(BuildConfig.NaverClientId, BuildConfig.NaverClientSecret, query, naverMap.locationOverlay.position.longitude.toString() + "," + naverMap.locationOverlay.position.latitude.toString())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .debounce(300, TimeUnit.MILLISECONDS)
                                .subscribe({ onNext ->
                                    if (onNext?.places != null) {
                                        suggestionAdapter.replaceAll(onNext.places)
                                        searchView.showSuggestions()
                                    } else {
                                        Toast.makeText(this@LocationSearchActivity, "결과가 없습니다. 다시 검색해주세요.", Toast.LENGTH_SHORT).show()
                                    }
                                }, { onError ->
                                    if (BuildConfig.DEBUG) {
                                        Toast.makeText(this@LocationSearchActivity, "[DEV] onQueryTextSubmit() check log", Toast.LENGTH_SHORT).show()
                                    }

                                    Timber.e("onQueryTextSubmit(): %s", onError.toString())
                                }))
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //Do some magic
                if (suggestionAdapter.count > 0) {
                    suggestionAdapter.replaceAll(ArrayList())
                    return true
                }

                return false
            }
        })
    }

    private fun setOnSearchListener() {
        searchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
            }

            override fun onSearchViewClosed() {
                suggestionAdapter.replaceAll(ArrayList())
                searchMarker.map = null
                searchInfo.map = null
            }
        })
    }

    override fun onClick(overlay: Overlay): Boolean {
        if (overlay.tag == "info") {
            Toast.makeText(this, "정보 클릭", Toast.LENGTH_SHORT).show()
            if (!location.isNullOrEmpty()) {
                val intent = Intent()
                intent.putExtra("location", location)
                intent.putExtra("locationName", locationName)
                intent.putExtra("x", x)
                intent.putExtra("y", y)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Timber.e("onInfoClicked : info empty //" + searchMarker.captionText)
            }
        } else {
            //wrong click
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)

        return true
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}