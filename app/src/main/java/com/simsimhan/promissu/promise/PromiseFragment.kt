package com.simsimhan.promissu.promise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.FragmentPromiseListBinding
import com.simsimhan.promissu.util.NavigationUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PromiseFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    private lateinit var binding: FragmentPromiseListBinding
    private lateinit var adapter: PromiseAdapter
    private val viewModel: PromiseViewModel by sharedViewModel()
    private var isPastPromise = false

    companion object {
        fun newInstance(position: Int, title: String, isPastPromise: Boolean): PromiseFragment {
            val fragment = PromiseFragment()
            val args = Bundle()
            args.putInt("Page_key", position)
            args.putString("Title_key", title)
            args.putBoolean("is_past_key", isPastPromise)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            isPastPromise = arguments!!.getBoolean("is_past_key")
        }

        adapter = PromiseAdapter(activity as AppCompatActivity?, arrayListOf(), isPastPromise)

        viewModel.errToastMsg.observe(this, Observer {
            binding.swipeContainer.isRefreshing = false
            toastMessage(it)
            NavigationUtil.replaceWithLoginView(activity as AppCompatActivity)
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPromiseListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@PromiseFragment
            viewModel = this@PromiseFragment.viewModel
            promiseRecyclerView.apply {
                adapter = this@PromiseFragment.adapter
                addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            }
            swipeContainer.apply {
                setOnRefreshListener(this@PromiseFragment)
                setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_dark, android.R.color.holo_orange_dark, android.R.color.holo_blue_dark)
                setProgressViewOffset(false, 0, resources.getDimensionPixelSize(R.dimen.refresher_start))
                post {
                    swipeContainer.isRefreshing = true
                    fetch(isPastPromise)
                }
            }
        }

        if(isPastPromise){
            viewModel.onListLoadedPast.observe(this, Observer {
                binding.swipeContainer.isRefreshing = false
                adapter.reset(it)
                setEmptyViewVisible(it.isEmpty())
            })
        }else{
            viewModel.onListLoadedRecent.observe(this, Observer {
                binding.swipeContainer.isRefreshing = false
                adapter.reset(it)
                setEmptyViewVisible(it.isEmpty())
            })
        }

        return binding.root
    }

    private fun setEmptyViewVisible(setVisible: Boolean) {
        binding.apply {
            emptyViewHolder.visibility = if (setVisible) View.VISIBLE else View.INVISIBLE
            promiseRecyclerView.setBackgroundColor(ContextCompat.getColor(promiseRecyclerView.context, if (isPastPromise) R.color.past_background_color else R.color.background_grey))
            emptyViewImage.setImageDrawable(ContextCompat.getDrawable(emptyViewImage.context, if (isPastPromise) R.drawable.no_appointment_red else R.drawable.no_appointment_blue))
            emptyViewHolder.setBackgroundColor(ContextCompat.getColor(emptyViewHolder.context, if (isPastPromise) R.color.past_background_color else R.color.background_grey))
            emptyViewText.setTextColor(ContextCompat.getColor(emptyViewText.context, if (isPastPromise) R.color.past_background_dark else R.color.colorStrong))
        }
    }

    override fun onResume() {
        super.onResume()
        fetch(isPastPromise)
    }

    override fun onRefresh() {
        fetch(isPastPromise)
    }

    private fun fetch(isPastPromise:Boolean) {
        viewModel.fetch(isPastPromise)
    }

    private fun toastMessage(msg:String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}