package com.simsimhan.promissu.ui.map

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityLocationMapBinding
import com.simsimhan.promissu.network.model.Promise

class LocationMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var binding: ActivityLocationMapBinding
    private lateinit var mapView: com.naver.maps.map.MapView
    private lateinit var naverMap: NaverMap
    private lateinit var searchMarker: Marker
    private lateinit var location: LocationOverlay
    private lateinit var locationSource: FusedLocationSource

    private val promise by lazy {
        intent?.extras?.getParcelable("promise") as Promise.Response?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()

        if (savedInstanceState == null) {
            mapView = binding.locationMapMapview
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        setupTextView()
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)
        initMyLocation(naverMap)
        initLocationMarker(naverMap)

    }

    private fun setupTextView(){
        binding.locationMapLocation.text = promise?.location
        binding.locationMapLocationName.text = promise?.location_name
    }

    private fun setupBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_map)
        binding.lifecycleOwner = this

        binding.locationMapBackButton.setOnClickListener {
            onBackPressed()
        }
        binding.locationMapCurrentLocationButton.setOnClickListener {
            naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
    }


    private fun initMyLocation(naverMap: NaverMap) {
        location = naverMap.locationOverlay
        location.icon = OverlayImage.fromResource(R.drawable.ic_icon_mylocation_overlay)
        location.circleColor = Color.parseColor("#48ef006d")
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
    }

    private fun initLocationMarker(naverMap: NaverMap){
        searchMarker = Marker()
        searchMarker.captionText = promise?.location_name?:"오류"
        searchMarker.position = LatLng(promise?.location_lat?:0.0, promise?.location_lon?:0.0)
        searchMarker.map = naverMap
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(searchMarker.position, 16.0)
        naverMap.moveCamera(cameraUpdate)
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