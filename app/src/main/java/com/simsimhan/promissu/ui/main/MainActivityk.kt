package com.simsimhan.promissu.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kakao.auth.Session
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityMainKBinding
import com.simsimhan.promissu.ui.main.adapter.MainAppointmentsAdapter
import com.simsimhan.promissu.ui.main.adapter.MainPendingAppointmentsAdapter
import com.simsimhan.promissu.ui.main.adapter.MainViewType
import com.simsimhan.promissu.ui.main.decorators.MainAppointmentsDecorator
import com.simsimhan.promissu.util.NavigationUtilk
import com.simsimhan.promissu.util.showSimpleDialog
import com.simsimhan.promissu.util.toastMsg
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class MainActivityk : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainKBinding
    private var uri: Uri? = null
//    private val uri by lazy { //다시 실행될때 문제.
//        intent.data
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isNewUser()||isTokenExpired()) {
            NavigationUtilk.replaceWithLoginView(this)
        }

//        viewModel.sendFcmTokenToServer()

//        uri = intent.data ?: null
//        uri?.let {
//                val execParamKey1 = it.getQueryParameter("roomid")
//                execParamKey1.let { roomId ->
//                    Timber.d("onCreate() param=roomid, key=%s", execParamKey1)
//                    enterInvitingRoom(roomId)
//                }
//        }

        setupBinding()
        setSupportActionBar(binding.mainToolbar)
        setupNavigation()
        setupActionbar()
        setupRecyclerView()
        setupObserves()

        Timber.d("@@@Token: %s", PromissuApplication.diskCache?.userToken)
        Timber.d("@@@UserI: %s", PromissuApplication.diskCache?.userId)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchRemoteData()

        uri = intent.data ?: null
        uri?.let {
            val execParamKey1 = it.getQueryParameter("roomid")
            execParamKey1.let { roomId ->
                Timber.d("onCreate() param=roomid, key=%s", execParamKey1)
                enterInvitingRoom(roomId)
                intent.apply {
                    replaceExtras(Bundle())
                    action = ""
                    data = null
                    flags = 0
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearTimer()
    }

    private fun isNewUser(): Boolean {
        return TextUtils.isEmpty(PromissuApplication.diskCache!!.userToken) && TextUtils.isEmpty(PromissuApplication.diskCache!!.userName)
    }

    private fun isTokenExpired(): Boolean {
        return if (Session.getCurrentSession().tokenInfo.remainingExpireTime <= 0) { //토큰 유효기간이 만료되었을 때
            Objects.requireNonNull(PromissuApplication.diskCache!!).clearUserData()
            true
        } else {
            false
        }
    }

    private fun enterInvitingRoom(roomID: String) {
        viewModel.enterAppointment(roomID)
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_k)
        binding.apply {
            viewModel = this@MainActivityk.viewModel
            lifecycleOwner = this@MainActivityk
        }
    }

    private fun setupRecyclerView() {
        binding.mainPromiseTodayRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = MainAppointmentsAdapter(this@MainActivityk, viewModel, viewModel, MainViewType.TODAY)
            addItemDecoration(MainAppointmentsDecorator(this@MainActivityk))
        }

        binding.mainPromisePendingRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = MainPendingAppointmentsAdapter(this@MainActivityk, viewModel)
            addItemDecoration(MainAppointmentsDecorator(this@MainActivityk))
        }
    }

    private fun setupNavigation() {
        binding.mainNavView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    showSimpleDialog(this, R.string.logout, R.string.okay, R.string.cancel, "정말 로그아웃 하시겠습니까?"
                    ) { _, _ ->
                        PromissuApplication.diskCache?.clearUserData()
                        NavigationUtilk.replaceWithLoginView(this)
                        binding.mainDrawerLayout.closeDrawers()
                    }
                    false
                }
                R.id.nav_past_appointments -> {
                    NavigationUtilk.openPastList(this)
                    false
                }
                else -> {
                    false
                }
            }
        }
        val profileImage = binding.mainNavView.getHeaderView(0).findViewById<ImageView>(R.id.profile_image)
        val profileName = binding.mainNavView.getHeaderView(0).findViewById<TextView>(R.id.profile_username)

        if (!PromissuApplication.diskCache?.profileThumbnail.isNullOrEmpty()) {
            val options = RequestOptions()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            profileImage.scaleType = ImageView.ScaleType.CENTER
            Glide.with(this)
                    .load(PromissuApplication.diskCache?.profileThumbnail)
                    .apply(options)
                    .into(profileImage)
        }

        profileName.text = PromissuApplication.diskCache?.userName
    }

    private fun setupActionbar() {
        val actionBar = supportActionBar
        actionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setupObserves() {
        viewModel.toastMsg.observe(this, Observer {
            toastMsg(this, it)
        })

        viewModel.onClickCreatePromise.observe(this, Observer {
            NavigationUtilk.openAddPromiseScreen(this@MainActivityk)
        })

        viewModel.onClickInvitingListButton.observe(this, Observer {
            NavigationUtilk.openInvitingList(this@MainActivityk)
        })

        viewModel.enterPromiseResponse.observe(this, Observer {
            NavigationUtilk.enterInvitingRoomWithoutActivityFinish(this@MainActivityk, it)
        })

        viewModel.openPendingActivity.observe(this, Observer {
            NavigationUtilk.enterPendingRoom(this, it)
        })

        viewModel.openDetailActivity.observe(this, Observer {
            NavigationUtilk.openPromiseDetailScreen(this, it)
        })
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                binding.mainDrawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}