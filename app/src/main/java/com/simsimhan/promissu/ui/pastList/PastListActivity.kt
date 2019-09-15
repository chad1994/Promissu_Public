package com.simsimhan.promissu.ui.pastList

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ActivityPastListBinding
import com.simsimhan.promissu.ui.invitingList.adapter.InvitingListAdapter
import com.simsimhan.promissu.ui.main.decorators.MainAppointmentsDecorator
import com.simsimhan.promissu.ui.pastList.adapter.PastListAdapter
import com.simsimhan.promissu.util.NavigationUtilk
import com.simsimhan.promissu.util.keyboardHide
import org.koin.androidx.viewmodel.ext.android.viewModel

class PastListActivity : AppCompatActivity(){

    private val viewModel by viewModel<PastListViewModel>()
    private lateinit var binding: ActivityPastListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setupActionbar()
        setupRecyclerView()
        setupObserves()

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_past_list)
        binding.apply {
            viewModel = this@PastListActivity.viewModel
            lifecycleOwner = this@PastListActivity
        }
    }

    private fun setupActionbar() {
        setSupportActionBar(binding.pastListToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
    }

    private fun setupRecyclerView() {
        binding.pastListRv.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = PastListAdapter(this@PastListActivity, viewModel)
            addItemDecoration(MainAppointmentsDecorator(this@PastListActivity))
        }
    }

    private fun setupObserves(){
        viewModel.onClickPastListItem.observe(this, Observer {
            NavigationUtilk.openPromiseDetailPastScreen(this@PastListActivity,it)
        })
    }
}