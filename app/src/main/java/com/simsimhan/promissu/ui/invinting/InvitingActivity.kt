package com.simsimhan.promissu.ui.invinting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityInvitingPromiseBinding
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.ui.invinting.adapter.InvitingParticipantAdapter
import com.simsimhan.promissu.ui.main.decorators.MainAppointmentsDecorator
import com.simsimhan.promissu.util.NavigationUtilk
import com.simsimhan.promissu.util.buildDialog
import com.simsimhan.promissu.util.keyboardHide
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvitingActivity : AppCompatActivity() {

    private val viewModel by viewModel<InvitingViewModel>()
    private lateinit var binding: ActivityInvitingPromiseBinding
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inviting_promise)
        binding.apply {
            viewModel = this@InvitingActivity.viewModel
            listener = this@InvitingActivity.viewModel
            lifecycleOwner = this@InvitingActivity
        }
    }

    private fun setupObserves() {
        viewModel.promise.observe(this, Observer {
            viewModel.fetchParticipants(it.id)
        })

        viewModel.confirmPromise.observe(this, Observer {
            this@InvitingActivity.finish()
        })

        viewModel.onClickLeftAppointment.observe(this, Observer {
            buildDialog(this,"약속을 정","나가시겠습니까?","취소","나가기", viewModel::leftAppointment)
        })

        viewModel.onClickDeleteAppointment.observe(this, Observer {
            buildDialog(this, "나가면 약속이 사라집니다","정말 나가시겠습니?","취소","나가기",viewModel::deleteAppointment)
        })

        viewModel.openLocationMap.observe(this, Observer {
            promise?.let {
                NavigationUtilk.openLocationMap(this, it)
            }
        })

        viewModel.finishActivity.observe(this, Observer {
            this.finish()
        })

    }


    private fun setupRecyclerView() {
        binding.invitingParticipantRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = InvitingParticipantAdapter(this@InvitingActivity)
            addItemDecoration(MainAppointmentsDecorator(this@InvitingActivity))
        }

    }

    private fun setupActionbar() {
        setSupportActionBar(binding.invitingToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
    }
}