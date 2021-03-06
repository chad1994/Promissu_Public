package com.simsimhan.promissu.ui.detail

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
import com.simsimhan.promissu.databinding.ViewMarkerAttendanceBinding
import com.simsimhan.promissu.network.AuthAPI
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.ui.detail.adapter.DetailUserStatusAdapter
import com.simsimhan.promissu.util.NavigationUtilk
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


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
    private lateinit var attendanceBinding: ViewMarkerAttendanceBinding
    private lateinit var atdBindingInflater: LayoutInflater
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
        atdBindingInflater = getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        attendanceBinding = ViewMarkerAttendanceBinding.inflate(atdBindingInflater)

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

        attendanceBinding.apply {
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
            if (promise.status == 2 && viewModel.myParticipation.get() != null && !(viewModel.isSocketOpen.value!!)) {
                viewModel.setSocketReady(true)
            }
        })

        viewModel.locationEvents.observe(this, Observer {
            viewModel.notifyEventInfo()
        })

        viewModel.attendedParticipants.observe(this, Observer {
            attendanceBinding.itemMarkerAttendanceText.text = "+" + (it.size - 1)
            attendanceMarker.icon = OverlayImage.fromView(attendanceBinding.root)

        })

        viewModel.dialogResponse.observe(this, Observer
        {
            //TODO : 나에게 온 요청일때 처리.
            buildResponseDialog()
        })

        viewModel.deleteAppointmentClicked.observe(this, Observer {
            if (it.admin_id != PromissuApplication.diskCache!!.userId) {
                buildLeftDialog(it.id)
            } else {
                buildDeleteDialog(it.id)
            }
        })

//        viewModel.sendLocationRequest.observe(this, Observer {
//            //TODO : 상대방에게 요청 클릭 시
//            buildRequestDialog(it.nickname, it.partId)
//        }) // 요청권 방식 변경으로 인한 기능 변경

        viewModel.userMarkers.observe(this, Observer
        {
            userMarkerList.forEach { existingMarker ->
                existingMarker.map = null
            }
            userMarkerList = it
            updateUserMarkers(userMarkerList)
        })

        viewModel.isSocketOpen.observe(this, Observer
        {
            if (it) {
                viewModel.startTimer()
                attendanceMarker.isVisible = true
                if(!PromissuApplication.diskCache!!.isEnteredDetailBefore){
                    openOnBoardingFragment()
                    PromissuApplication.diskCache!!.isEnteredDetailBefore = true
                }
            } else {
                viewModel.removeTimer()
            }
        })

        viewModel.longPressed.observe(this, Observer
        {
            //            TODO : ..
        })

        viewModel.modifyButtonClicked.observe(this, Observer
        {
            NavigationUtilk.openModifyPromiseScreen(this, viewModel.response.value!!)
        })

        viewModel.cameraMoveToTarget.observe(this, Observer {
            val cameraUpdate = CameraUpdate.scrollAndZoomTo(it, naverMap.cameraPosition.zoom)
            naverMap.moveCamera(cameraUpdate)
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
            initAttendanceMarker(it, naverMap)
        })

        viewModel.trackingMode.observe(this, Observer {
            when (it) {
                1 -> naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
                2 -> naverMap.locationTrackingMode = LocationTrackingMode.Follow
                3 -> naverMap.locationTrackingMode = LocationTrackingMode.Face
            }
        })



    }

    private fun openOnBoardingFragment(){
        val onBoardingFragment = DetailOnBoardingFragment.newInstance()
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
//                .setCustomAnimations(
//                        R.anim.slide_in_top,
//                        R.anim.slide_out_top,
//                        R.anim.slide_in_bottom,
//                        R.anim.slide_out_bottom
//                )
        transaction.add(binding.detailActivityContainer.id, onBoardingFragment, "OnBoardingFragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }


    private fun initTargetLocation(it: LatLng, naverMap: NaverMap) {
        meetingMarker.apply {
            position = it
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_icon_location)
            anchor = PointF(0.1f, 1.0f)
            setOnClickListener {
                openAttendanceFragment()
                true
            }
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

    private fun initAttendanceMarker(it: LatLng, naverMap: NaverMap) {
//        val attendMarker = LayoutInflater.from(this).inflate(R.layout.view_marker_attendance, null)
        attendanceBinding.itemMarkerAttendanceText.text = "+0"
        attendanceMarker.apply {
            position = it
            map = naverMap
            icon = OverlayImage.fromView(attendanceBinding.root)
            anchor = PointF(0.5f, 0f)
            isVisible = false
            setOnClickListener {
                openAttendanceFragment()
                true
            }
        }
    }

    private fun openAttendanceFragment() {
        binding.detailActivityContainer.setBackgroundColor(resources.getColor(R.color.mdtp_transparent_black))
        val detailAttendanceFragment = DetailAttendanceFragment.newInstance()
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_top,
                        R.anim.slide_out_top,
                        R.anim.slide_in_bottom,
                        R.anim.slide_out_bottom
                )
        transaction.add(binding.detailActivityContainer.id, detailAttendanceFragment, "AttendanceFragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun initMyLocationViewResource() {

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

    private fun buildLeftDialog(room_id: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_request)
        val text1 = dialog.findViewById<TextView>(R.id.dialog_text1)
        val text2 = dialog.findViewById<TextView>(R.id.dialog_text2)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_button_cancel)
        val btnAccept = dialog.findViewById<Button>(R.id.dialog_button_accept)
        text1.text = "정말 약속을"
        text2.text = "나가시겠습니까?"
        btnAccept.text = "나가기"
        btnCancel.text = "취소"
        btnAccept.setOnClickListener {
            // successfully delete room
            viewModel.addDisposable(
                    PromissuApplication.retrofit!!
                            .create(AuthAPI::class.java)
                            .leftAppointment(PromissuApplication.getVersionInfo(), "Bearer " + PromissuApplication.diskCache!!.userToken, room_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ onNext ->
                                if (onNext.code() == 200) {
                                    //
                                    if (!BuildConfig.DEBUG) {
                                        val eventParams = Bundle()
                                        eventParams.putInt("room_id", room_id)
                                        eventParams.putLong("user_id", PromissuApplication.diskCache!!.userId)
                                        PromissuApplication.firebaseAnalytics!!.logEvent("appointment_leave", eventParams)
                                    }
                                    dialog.dismiss()
                                    onBackPressed()
                                } else if (onNext.code() == 401) {
                                    Toast.makeText(this, "나가기 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "약속 나가기에 실패 했습니다. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }, {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(this, "나가기 에러", Toast.LENGTH_SHORT).show()
                                }
                                dialog.dismiss()
                            }))
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun buildDeleteDialog(room_id: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_request)
        val text1 = dialog.findViewById<TextView>(R.id.dialog_text1)
        val text2 = dialog.findViewById<TextView>(R.id.dialog_text2)
        val btnCancel = dialog.findViewById<Button>(R.id.dialog_button_cancel)
        val btnAccept = dialog.findViewById<Button>(R.id.dialog_button_accept)
        text1.text = "정말 약속을"
        text2.text = "삭제 하시겠습니까?"
        btnAccept.text = "삭제"
        btnCancel.text = "취소"
        btnAccept.setOnClickListener {
            // successfully delete room
            viewModel.addDisposable(
                    PromissuApplication.retrofit!!
                            .create(AuthAPI::class.java)
                            .deleteAppointment(PromissuApplication.getVersionInfo(), "Bearer " + PromissuApplication.diskCache!!.userToken, room_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ onNext ->
                                if (onNext.code() == 200) {
                                    //
                                    if (!BuildConfig.DEBUG) {
                                        val eventParams = Bundle()
                                        eventParams.putInt("room_id", room_id)
                                        eventParams.putLong("user_id", PromissuApplication.diskCache!!.userId)
                                        PromissuApplication.firebaseAnalytics!!.logEvent("appointment_delete", eventParams)
                                    }
                                    dialog.dismiss()
                                    onBackPressed()
                                } else if (onNext.code() == 401) {
                                    Toast.makeText(this, "삭제 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "나가기에 실패 했습니다. 잠시후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                                }
                            }, {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(this, "삭제 에러", Toast.LENGTH_SHORT).show()
                                }
                                dialog.dismiss()
                            }))
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
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
        if (requestCode == NavigationUtilk.REQUEST_MODIFY_PROMISE && resultCode == Activity.RESULT_OK) {
            val promise = data!!.getParcelableExtra<Promise.Response>("promise")
            viewModel.updateResponseData(promise)
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
            binding.detailActivityContainer.setBackgroundColor(resources.getColor(R.color.zxing_transparent))
        }
    }
}