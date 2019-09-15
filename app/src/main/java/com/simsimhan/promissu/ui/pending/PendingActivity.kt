package com.simsimhan.promissu.ui.pending

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityPendingActivityBinding
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.ui.invinting.adapter.InvitingParticipantAdapter
import com.simsimhan.promissu.ui.main.decorators.MainAppointmentsDecorator
import com.simsimhan.promissu.util.NavigationUtilk
import com.simsimhan.promissu.util.keyboardHide
import org.koin.androidx.viewmodel.ext.android.viewModel

class PendingActivity : AppCompatActivity(){

    private val viewModel by viewModel<PendingViewModel>()
    private lateinit var binding: ActivityPendingActivityBinding
    private val promise by lazy {
        intent?.extras?.getParcelable("promise") as Promise.Response?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setupActionbar()
        setupRecyclerView()
        setupObserves()

        promise?.let { viewModel.fetchPromiseData(promise!!) }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
                keyboardHide()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pending_activity)
        binding.apply {
            lifecycleOwner = this@PendingActivity
            viewModel = this@PendingActivity.viewModel
            listener = this@PendingActivity.viewModel
        }
    }

    private fun setupObserves() {

        viewModel.promise.observe(this, Observer {
            viewModel.fetchParticipants(it.id)
        })

        viewModel.openLocationMap.observe(this, Observer {
            promise?.let {
                NavigationUtilk.openLocationMap(this, it)
            }
        })
    }

    private fun setupActionbar(){
        setSupportActionBar(binding.pendingToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupRecyclerView() {
        binding.pendingParticipantRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = InvitingParticipantAdapter(this@PendingActivity)
            addItemDecoration(MainAppointmentsDecorator(this@PendingActivity))
        }

    }



}