package com.simsimhan.promissu.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemDetailUserStatusBinding
import com.simsimhan.promissu.network.model.Participant

class DetailUserStatusAdapter(
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<DetailUserStatusAdapter.DetailUserStatusViewHolder>() {


    private var list = listOf<Participant.Response>()

    fun setData(list: List<Participant.Response>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailUserStatusViewHolder {
        val binding = ItemDetailUserStatusBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailUserStatusViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DetailUserStatusViewHolder, position: Int) {
        holder.bind(lifecycleOwner, list[position])
    }


    class DetailUserStatusViewHolder(private val itemBinding: ItemDetailUserStatusBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(_lifecycleOwner: LifecycleOwner, _participant: Participant.Response) {
            itemBinding.apply {
                lifecycleOwner = _lifecycleOwner
                participants = _participant
                executePendingBindings()
            }
        }
    }
}