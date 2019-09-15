package com.simsimhan.promissu.ui.pastList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemPastListBinding
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.pastList.PastListEventListener

class PastListAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val eventListener: PastListEventListener
) : RecyclerView.Adapter<PastListViewHolder>() {

    private var list = listOf<PromiseResponse>()

    fun setData(list: List<PromiseResponse>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastListViewHolder {
        val binding = ItemPastListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PastListViewHolder(binding, eventListener, lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PastListViewHolder, position: Int) {
        holder.bind(list[position])
    }
}


class PastListViewHolder(
        private val itemBinding: ItemPastListBinding,
        private val eventListener: PastListEventListener,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(promise: PromiseResponse) {
        itemBinding.promise = promise
        itemBinding.lifecycleOwner = lifecycleOwner
        itemBinding.listener = eventListener
    }
}