package com.simsimhan.promissu.ui.pastdetail

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemDetailPastRankingBinding

class DetailPastAdapter(
        private val lifecycleOwner: LifecycleOwner,
        private val viewModel: DetailPastViewModel
) : RecyclerView.Adapter<DetailPastAdapter.DetailPastViewHolder>() {

//    private var list = listOf<Ranking>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPastViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: DetailPastViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class DetailPastViewHolder(private val itemBinding: ItemDetailPastRankingBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(){

        }
    }
}