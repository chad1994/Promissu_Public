package com.simsimhan.promissu.ui.pastdetail

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityDetailPastPromiseBinding
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.pastdetail.adapter.DetailPastAdapter
import com.simsimhan.promissu.util.NavigationUtilk

class DetailPastActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailPastViewModel
    private lateinit var binding: ActivityDetailPastPromiseBinding
    private lateinit var promise: PromiseResponse
    private lateinit var factory: DetailPastViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_past_promise)
        promise = intent.getParcelableExtra("promise")
        factory = DetailPastViewModelFactory(promise)
        viewModel = ViewModelProviders.of(this, factory).get(DetailPastViewModel::class.java)

        binding.apply {
            viewModel = this@DetailPastActivity.viewModel
            lifecycleOwner = this@DetailPastActivity
        }

        binding.detailPastRankingRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = DetailPastAdapter(this@DetailPastActivity, this@DetailPastActivity.viewModel)
        }

        setSupportActionBar(binding.detailPastToolbar)
        changeStatusBarColor()

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }

        setupObserves()

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupObserves(){
        viewModel.openLocationMap.observe(this, Observer {
            NavigationUtilk.openLocationMap(this,promise.promise)
        })
    }

}