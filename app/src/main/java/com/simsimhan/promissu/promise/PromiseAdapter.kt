package com.simsimhan.promissu.promise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.ViewPromiseRowItemBinding
import com.simsimhan.promissu.network.model.Promise

class PromiseAdapter(
        private val items: MutableList<Promise.Response>,
        private val isPastPromise: Boolean,
        private val lifecycleOwner: LifecycleOwner,
        private val viewModel: PromiseViewModel)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_PROFILE = 1
        private const val VIEW_TYPE_ITEM = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_PROFILE) {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_promise_row_profile, parent, false)
            ProfileViewHolder(itemView)
        } else {
            val binding = ViewPromiseRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(viewModel,items[position],isPastPromise,lifecycleOwner,viewModel)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun reset(onNext: List<Promise.Response>) {
        items.clear()
        items.addAll(onNext)
        notifyDataSetChanged()
    }


    internal class ItemViewHolder(private val itemBinding: ViewPromiseRowItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind( _viewModel: PromiseViewModel, _response: Promise.Response,_isPastPromise: Boolean,_lifecycleOwner: LifecycleOwner,  _listener: PromiseItemEventListener) {
            itemBinding.apply {
                response = _response
                lifecycleOwner = _lifecycleOwner
                viewModel = _viewModel
                isPast = _isPastPromise
                listener = _listener
                executePendingBindings()
            }
        }

    }

    internal class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}