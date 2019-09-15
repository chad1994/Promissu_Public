package com.simsimhan.promissu.ui.invitingList

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityInvitingPromiseListBinding
import com.simsimhan.promissu.ui.invitingList.adapter.InvitingListAdapter
import com.simsimhan.promissu.ui.main.decorators.MainAppointmentsDecorator
import com.simsimhan.promissu.util.NavigationUtilk
import com.simsimhan.promissu.util.keyboardHide
import org.koin.androidx.viewmodel.ext.android.viewModel

class InvitingListActivity : AppCompatActivity(){

    private val viewModel by viewModel<InvitingListViewModel>()
    private lateinit var binding: ActivityInvitingPromiseListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setupActionbar()
        setupRecyclerView()
        setupObserves()

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchInvitingList()
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inviting_promise_list)
        binding.apply {
            viewModel = this@InvitingListActivity.viewModel
            lifecycleOwner = this@InvitingListActivity
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(binding.invitingListToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupRecyclerView() {
        binding.invitingListRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = InvitingListAdapter(this@InvitingListActivity, viewModel)
            addItemDecoration(MainAppointmentsDecorator(this@InvitingListActivity))
        }
    }

    private fun setupObserves(){
        viewModel.onClickInvitingListItem.observe(this, Observer {
            NavigationUtilk.enterInvitingRoomWithoutActivityFinish(this@InvitingListActivity,it)
        })
    }
}