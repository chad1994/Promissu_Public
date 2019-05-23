package com.simsimhan.promissu.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityDetailPromiseBinding
import com.simsimhan.promissu.detail.adapter.DetailUserStatusAdapter
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.NavigationUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


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
    //    private lateinit var currentFragment: Fragment
    private lateinit var promise: Promise.Response
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mapView: com.naver.maps.map.MapView
    private lateinit var naverMap: NaverMap
    private lateinit var location: LocationOverlay
    //    private lateinit var arriveView: View
    private lateinit var notArriveView: View
    private var userMarkerList = emptyList<Marker>()
    private var locationFlag = false //최초 현위치 갱신 시.
    private var meetingMarker = Marker()
    private var attendanceMarker = Marker()
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
        binding.detailActivityBottomSheet.bsScrollBtn.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


        viewModel.onBackPressed.observe(this, Observer {
            onBackPressed()
        })

        viewModel.toastMsg.observe(this, Observer {
            ToastMessage(it)
        })

        viewModel.participants.observe(this, Observer {
            if (promise.status == 1 && viewModel.myParticipation.get() != null) { //방이 pending 되고 참여자 정보를 받아왔을 때
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

        viewModel.userMarkers.observe(this, Observer {
            userMarkerList.forEach { existingMarker ->
                existingMarker.map = null
            }
            userMarkerList = it
            updateUserMarkers(userMarkerList)
        })

        viewModel.isSocketOpen.observe(this, Observer {
            if (it) {
                viewModel.startTimer()
            } else {
                viewModel.removeTimer()
            }
        })

        viewModel.longPressed.observe(this, Observer {
//            TODO : ..
        })

        viewModel.modifyButtonClicked.observe(this, Observer {
            NavigationUtil.openModifyPromiseScreen(this,promise)
        })
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource

        viewModel.setNaverMap(naverMap)
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BUILDING, true)

        initMyLocation(naverMap)
        naverMap.addOnLocationChangeListener {
            if (!locationFlag) {
                viewModel.fetchParticipants()
                locationFlag = true
            }
            val myLatlng = LatLng(it.latitude, it.longitude)
            if (myLatlng.distanceTo(meetingMarker.position) <= 100) {
                viewModel.checkArrive(true)
            } else {
                viewModel.checkArrive(false)
            }
        }



        viewModel.meetingLocation.observe(this, Observer {
            initTargetLocation(it, naverMap)
            initAttendanceMarker(it,naverMap)
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
            icon = OverlayImage.fromResource(R.drawable.ic_icon_location)
            anchor = PointF(0.1f, 1.0f)
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

    private fun initAttendanceMarker(it:LatLng,naverMap: NaverMap){
        val attendMarker = LayoutInflater.from(this).inflate(R.layout.view_marker_attendance, null)
        attendanceMarker.apply {
            position = it
            map = naverMap
            icon = OverlayImage.fromView(attendMarker)
            anchor = PointF(0.5f, 0f)
        }
    }

    private fun initMyLocationViewResource() {
//        arriveView = LayoutInflater.from(this).inflate(R.layout.user_marker, null)
//        arriveView.setOnTouchListener { v, event ->
//            Toast.makeText(this,"asdf",Toast.LENGTH_SHORT).show()
//            false
//        }
//        val tv = arriveView.findViewById(R.id.user_marker_name) as TextView
//        val img = arriveView.findViewById(R.id.user_marker_image) as ImageView
//        tv.text = "출석하기"
//        tv.setTextColor(Color.parseColor("#5F6CCC"))
//        img.setImageResource(R.drawable.ic_icon_marker_attend)

        notArriveView = LayoutInflater.from(this).inflate(R.layout.user_marker, null)
        val ntv = notArriveView.findViewById(R.id.user_marker_name) as TextView
        val nImg = notArriveView.findViewById(R.id.user_marker_image) as ImageView
        ntv.text = "나"
        nImg.setImageResource(R.drawable.ic_icon_marker_default)
    }

    private fun initMyLocation(naverMap: NaverMap) {
//        initMyLocationViewResource()
        location = naverMap.locationOverlay
        location.icon = OverlayImage.fromResource(R.drawable.ic_icon_mylocation_overlay)
        location.circleColor = Color.parseColor("#48ef006d")
//        val myView = LayoutInflater.from(this).inf
// late(R.layout.user_marker, null)
//        val tv = myView.findViewById(R.id.user_marker_name) as TextView
//        tv.text = "나"
//        location.icon = OverlayImage.fromView(notArriveView)
//        location.anchor = PointF(0.5f, 1f)

        binding.detailAttendButton.setOnClickListener {
            if (viewModel.isArrive.value == true && viewModel.isSocketOpen.value == true) {
                buildAttendDialog(location.position.longitude, location.position.latitude)
            }
        }
    }

    private fun updateUserMarkers(markers: List<Marker>) {
        markers.forEach {
            val myView = LayoutInflater.from(this).inflate(R.layout.user_marker, null)
            val tv = myView.findViewById(R.id.user_marker_name) as TextView
            val bg = myView.findViewById(R.id.user_marker_image) as ImageView
            bg.setImageResource(R.drawable.ic_icon_marker_default)
            it.map = naverMap
            tv.text = it.tag.toString()
            it.icon = OverlayImage.fromView(myView)
            it.anchor = PointF(0.5f, 1f)
        }
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
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun buildResponseDialog() {
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
            viewModel.sendLocationResponse(naverMap.locationOverlay.position.longitude, naverMap.locationOverlay.position.latitude)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            viewModel.sendLocationReject()
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun buildAttendDialog(lon: Double, lat: Double) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_request)
        val text1 = dialog.findViewById<TextView>(R.id.dialog_text1)
        val text2 = dialog.findViewById<TextView>(R.id.dialog_text2)
        val btnCancel = dialog.findViewById<TextView>(R.id.dialog_button_cancel)
        val btnAccept = dialog.findViewById<TextView>(R.id.dialog_button_accept)
        text1.text = "지금 바로"
        text2.text = "출석하시겠습니까?"
        btnAccept.text = "출석"
        btnCancel.text = "취소"
        btnAccept.setOnClickListener {
            viewModel.sendLocationAttend(lon, lat)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun ToastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
        viewModel.removeTimer()
        binding.detailTimer.visibility = View.GONE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NavigationUtil.REQUEST_MODIFY_PROMISE && resultCode == Activity.RESULT_OK) {
            val promise = data!!.getParcelableExtra<Promise.Response>("promise")
            viewModel.updateResponseData(promise)
        }
    }
}