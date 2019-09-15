package com.simsimhan.promissu.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemAppointmentTodayElseBinding
import com.simsimhan.promissu.databinding.ItemAppointmentTodayEmptyBinding
import com.simsimhan.promissu.databinding.ItemAppointmentTodayFirstBinding
import com.simsimhan.promissu.network.model.PromiseResponse
import com.simsimhan.promissu.ui.main.MainEventListener
import com.simsimhan.promissu.ui.main.MainViewModel

class MainAppointmentsAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val eventListener: MainEventListener,
        private val viewModel : MainViewModel,
        private val viewType: MainViewType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = listOf<PromiseResponse>()

    private companion object {
        const val FIRST = 0
        const val ELSE = 1
        const val EMPTY = -1
    }

    fun setData(list: List<PromiseResponse>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            MainViewType.TODAY -> {
                return if(position == 0&&list[0].promise.id!=0){
                    FIRST
                }else if(position == 0 &&list[0].promise.id==0){
                    EMPTY
                }
                else{
                    ELSE
                }
            }
            MainViewType.ELSE -> ELSE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         return when (viewType) {
            FIRST -> {
                val binding =
                        ItemAppointmentTodayFirstBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MainTodayAppointmentsViewHolder(binding, viewModel, eventListener, lifecycleOwner)
            }
             EMPTY -> {
                 val binding = ItemAppointmentTodayEmptyBinding.inflate(LayoutInflater.from(parent.context),parent ,false)
                 MainTodayEmptyViewHolder(binding)
             }
            else -> {
                val binding =
                        ItemAppointmentTodayElseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MainElseAppointmentsViewHolder(binding, eventListener, lifecycleOwner)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is MainTodayAppointmentsViewHolder){
            (holder).bind(list[position])
        }else if(holder is MainElseAppointmentsViewHolder){
            (holder).bind(list[position])
        }else{
            //nothing
        }
    }
}

class MainTodayAppointmentsViewHolder(
        private val itemBinding: ItemAppointmentTodayFirstBinding,
        private val viewModel : MainViewModel,
        private val eventListener: MainEventListener,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(promise: PromiseResponse) {
        itemBinding.promise = promise
        itemBinding.viewModel = viewModel
        itemBinding.lifecycleOwner = lifecycleOwner
        itemBinding.listener = eventListener
    }
}

class MainElseAppointmentsViewHolder(
        private val itemBinding: ItemAppointmentTodayElseBinding,
        private val eventListener: MainEventListener,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(itemBinding.root){
    fun bind(promise: PromiseResponse) {
        itemBinding.promise = promise
        itemBinding.lifecycleOwner = lifecycleOwner
        itemBinding.listener = eventListener
    }
}

class MainTodayEmptyViewHolder(
        private val itemBinding : ItemAppointmentTodayEmptyBinding
) : RecyclerView.ViewHolder(itemBinding.root){

}

enum class MainViewType {
    TODAY, ELSE
}

