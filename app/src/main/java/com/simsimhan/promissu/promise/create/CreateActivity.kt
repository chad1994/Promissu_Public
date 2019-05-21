package com.simsimhan.promissu.promise.create

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.simsimhan.promissu.BuildConfig
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityCreatePromiseBinding
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.util.NavigationUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateActivity : AppCompatActivity() {
    private val NUM_ITEMS = 3
    private val DEBUG = BuildConfig.DEBUG
    private val TAG = "PromiseCreateActivity"
    private lateinit var firstFragment: CreateFragment
    private lateinit var secondFragment: CreateFragment
    private lateinit var thirdFragment: CreateFragment
    private lateinit var binding: ActivityCreatePromiseBinding
    private lateinit var adapterViewPager: CreateFragmentPagerAdapter
    private var promise :Promise.Response? = null
    private val viewModel: CreateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_promise)
        binding.apply {
            viewModel = this@CreateActivity.viewModel
            lifecycleOwner = this@CreateActivity
        }

        if(intent.extras!=null){
            promise = intent.getParcelableExtra("promise")
            viewModel.setModify(true,promise!!.id)
        }

        if (!PromissuApplication.diskCache!!.isUploadedPromiseBefore) {
            Toast.makeText(this, "좌우로 미세요.", Toast.LENGTH_LONG).show()
        }

        adapterViewPager = CreateFragmentPagerAdapter(supportFragmentManager)
        binding.toolbar.setTitleTextColor(resources.getColor(R.color.black))
        binding.vpPager.adapter = adapterViewPager
        binding.vpPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                viewModel.setToolbarTitle(position)
            }
        })
        setSupportActionBar(binding.toolbar)
        changeStatusBarColor()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        viewModel.toastMessage.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.response.observe(this, Observer {
            NavigationUtil.enterRoom(this, it)
            finish()
        })

        viewModel.modifyResponse.observe(this, Observer {
            if(it){
                val intent = Intent()
                intent.putExtra("promise",it) // TODO : 응답 메세지를 보내는 형태로 변경 되어야 함 .
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun changeStatusBarColor() {
        val window = window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.statusBarColor)
        }
    }

    inner class CreateFragmentPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val fragment: Fragment? = null
            return when (position) {
                    0, 1, 2 -> {
                        if(promise==null){
                        CreateFragment.newInstance(position)
                        }else{
                            CreateFragment.newInstance(position,promise!!)
                        }
                    }
                    else -> fragment!!
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Fragment {
            val createdFragment = super.instantiateItem(container, position) as Fragment

            when (position) {
                0 -> firstFragment = createdFragment as CreateFragment
                1 -> secondFragment = createdFragment as CreateFragment
                2 -> thirdFragment = createdFragment as CreateFragment
            }

            return createdFragment
        }

        override fun getCount(): Int {
            return NUM_ITEMS
        }
    }

}