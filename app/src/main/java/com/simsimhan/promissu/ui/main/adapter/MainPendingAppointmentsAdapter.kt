package com.simsimhan.promissu.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemAppointmentPendingBinding
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.main.MainEventListener
import com.simsimhan.promissu.ui.main.MainViewModel

class MainPendingAppointmentsAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val eventListener: MainEventListener
) : RecyclerView.Adapter<MainPendingAppointmentsViewHolder>() {

    private var list = listOf<PromiseResponse>()

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(list: List<PromiseResponse>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainPendingAppointmentsViewHolder {
        val binding = ItemAppointmentPendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainPendingAppointmentsViewHolder(binding,eventListener,lifecycleOwner)
    }

    override fun onBindViewHolder(holder: MainPendingAppointmentsViewHolder, position: Int) {
        holder.bind(list[position])
    }
}

class MainPendingAppointmentsViewHolder(
        private val itemBinding: ItemAppointmentPendingBinding,
        private val eventListener: MainEventListener,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(promise: PromiseResponse) {
        itemBinding.promise = promise
        itemBinding.lifecycleOwner = lifecycleOwner
        itemBinding.listener = eventListener
    }
}