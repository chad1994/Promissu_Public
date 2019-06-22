package com.simsimhan.promissu.ui.pastdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemDetailPastRankingBinding
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.ui.pastdetail.DetailPastViewModel

class DetailPastAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val viewModel: DetailPastViewModel
) : RecyclerView.Adapter<DetailPastAdapter.DetailPastViewHolder>() {

    private var list = listOf<Participant.Response>()

    fun setData(list: List<Participant.Response>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPastViewHolder {
        val binding = ItemDetailPastRankingBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailPastAdapter.DetailPastViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DetailPastViewHolder, position: Int) {
        holder.bind(lifecycleOwner, list[position], viewModel, position)
    }


    class DetailPastViewHolder(private val itemBinding: ItemDetailPastRankingBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(_lifecycleOwner: LifecycleOwner, _participant: Participant.Response, _viewModel: DetailPastViewModel, _position: Int) {
            itemBinding.apply {
                lifecycleOwner = _lifecycleOwner
                participant = _participant
                viewModel = _viewModel
                position = _position
                executePendingBindings()
            }
        }
    }
}