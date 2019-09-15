package com.simsimhan.promissu.ui.invitingList.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemInvitingListBinding
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.invitingList.InvitingListListener


class InvitingListAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val eventListener: InvitingListListener
) : RecyclerView.Adapter<InvitingListViewHolder>() {

    private var list = listOf<PromiseResponse>()

    fun setData(list: List<PromiseResponse>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitingListViewHolder {
        val binding = ItemInvitingListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InvitingListViewHolder(binding,eventListener,lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: InvitingListViewHolder, position: Int) {
        holder.bind(list[position])
    }
}


class InvitingListViewHolder(
        private val itemBinding: ItemInvitingListBinding,
        private val eventListener: InvitingListListener,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(promise: PromiseResponse) {
        itemBinding.promise = promise
        itemBinding.lifecycleOwner = lifecycleOwner
        itemBinding.listener = eventListener
    }
}