package com.simsimhan.promissu.ui.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemDetailUserRankingBinding
import com.simsimhan.promissu.network.model.Participant
import com.simsimhan.promissu.ui.detail.DetailViewModel

class DetailAttendanceAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val viewModel: DetailViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = listOf<Participant.Response>()

    fun setData(list: List<Participant.Response>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemDetailUserRankingBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailAttendanceAdapter.DetailAttendanceViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DetailAttendanceAdapter.DetailAttendanceViewHolder).bind(lifecycleOwner, position, list[position], viewModel)
    }


    class DetailAttendanceViewHolder(private val itemBinding: ItemDetailUserRankingBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(_lifecycleOwner: LifecycleOwner,_ranking:Int, _participant: Participant.Response, _viewModel: DetailViewModel) {
            itemBinding.apply {
                lifecycleOwner = _lifecycleOwner
                participant = _participant
                ranking = _ranking
                viewModel = _viewModel
                executePendingBindings()
            }
        }
    }
}