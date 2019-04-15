package com.simsimhan.promissu.detail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityDetailPromiseBinding
import com.simsimhan.promissu.detail.adapter.DetailUserStatusAdapter
import com.simsimhan.promissu.network.model.Promise


class PromiseDetailActivity : AppCompatActivity(), OnMapReadyCallback {


    private val DEBUG = BuildConfig.DEBUG
    private val TAG = "PromiseDetailActivity"

    companion object {
        //        private const val DETAIL_CONTAINER_ID = R.id.detail_activity_container
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private lateinit var viewModel: DetailViewModel
    private lateinit var factory: DetailViewModelFactory
    private lateinit var binding: ActivityDetailPromiseBinding
    private lateinit var currentFragment: Fragment
    private lateinit var promise: Promise.Response
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapView: com.naver.maps.map.MapView
    private lateinit var naverMap: NaverMap
    private var meetingMarker = Marker()
    private var meetingCircle = CircleOverlay()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_promise)
        promise = intent.getParcelableExtra("promise")
        factory = DetailViewModelFactory(promise)
        viewModel = ViewModelProviders.of(this, factory).get(DetailViewModel::class.java)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        if (savedInstanceState == null) {
            mapView = binding.detailActivityMapview
        }
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.apply {
            viewModel = this@PromiseDetailActivity.viewModel
            lifecycleOwner = this@PromiseDetailActivity
        }


        binding.detailBottomRv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = DetailUserStatusAdapter(this@PromiseDetailActivity, this@PromiseDetailActivity.viewModel, this@PromiseDetailActivity.viewModel)
        }


        val bottomSheetBehavior = BottomSheetBehavior.from(binding.detailActivityBottomSheet.root)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == 4) {
                    viewModel.setSpreadState(false)
                } else if (newState == 3) {
                    viewModel.setSpreadState(true)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


        viewModel.onBackPressed.observe(this, Observer {
            onBackPressed()
        })

        viewModel.participants.observe(this, Observer {
            if (promise.status == 1) {
                viewModel.setSocketReady(true)
            }
        })

        viewModel.locationEvents.observe(this, Observer {
            viewModel.notifyEventInfo()
        })

        viewModel.dialogResponse.observe(this, Observer {
            //TODO : 나에게 온 요청일때 처리.
            buildResponseDialog()
        })

        viewModel.sendLocationRequest.observe(this, Observer {
            //TODO : 상대방에게 요청 클릭 시
            buildRequestDialog(it.nickname, it.partId)
        })
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        viewModel.setNaverMap(naverMap)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)

//        val marker = Marker()
//        marker.position = LatLng(37.5670135, 126.9783740)
//        marker.map = naverMap
//        val view = LayoutInflater.from(this).inflate(R.layout.user_marker, null)
//        val tv = view.findViewById(R.id.user_marker_name) as TextView
//        tv.text = "태성"
//        marker.icon = OverlayImage.fromView(view)
        initMyLocation(naverMap)

        viewModel.meetingLocation.observe(this, Observer {
            initTargetLocation(it, naverMap)
        })

        viewModel.trackingMode.observe(this, Observer {
            when (it) {
                1 -> naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
                2 -> naverMap.locationTrackingMode = LocationTrackingMode.Follow
                3 -> naverMap.locationTrackingMode = LocationTrackingMode.Face
            }
        })

    }


    private fun initTargetLocation(it: LatLng, naverMap: NaverMap) {
        meetingMarker.apply {
            position = it
            map = naverMap
        }
        meetingCircle.apply {
            center = it
            radius = 100.0
            map = naverMap
            color = Color.parseColor("#11ff0068")
        }
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(it, 16.0)
        naverMap.moveCamera(cameraUpdate)

    }

    private fun initMyLocation(naverMap: NaverMap) {
        val location = naverMap.locationOverlay
        val myView = LayoutInflater.from(this).inflate(R.layout.user_marker, null)
        val tv = myView.findViewById(R.id.user_marker_name) as TextView
        tv.text = "나"
        location.icon = OverlayImage.fromView(myView)
        location.anchor = PointF(0.5f, 1f)
    }

    private fun buildRequestDialog(nickname: String, partId: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_request)
        val text1 = dialog.findViewById<TextView>(R.id.dialog_text1)
        val text2 = dialog.findViewById<TextView>(R.id.dialog_text2)
        val btnCancel = dialog.findViewById<TextView>(R.id.dialog_button_cancel)
        val btnAccept = dialog.findViewById<TextView>(R.id.dialog_button_accept)
        text1.text = nickname + "님에게 위치를"
        text2.text = "요청하시겠습니까?"
        btnAccept.setOnClickListener {
            viewModel.sendLocationRequest(partId)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun buildResponseDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_request)
        val text1 = dialog.findViewById<TextView>(R.id.dialog_text1)
        val text2 = dialog.findViewById<TextView>(R.id.dialog_text2)
        val btnCancel = dialog.findViewById<TextView>(R.id.dialog_button_cancel)
        val btnAccept = dialog.findViewById<TextView>(R.id.dialog_button_accept)
        text1.text = "누군가 위치를 요청해요!"
        text2.text = "수락하시겠습니까?"
        btnAccept.text = "수락"
        btnCancel.text = "거절"
        btnAccept.setOnClickListener {
            viewModel.sendLocationResponse(naverMap.locationOverlay.position.longitude,naverMap.locationOverlay.position.latitude)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
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
}