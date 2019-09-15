package com.simsimhan.promissu.ui.invinting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simsimhan.promissu.databinding.ItemInvitingParticipantBinding
import com.simsimhan.promissu.network.model.Participant



class InvitingParticipantAdapter(
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<InvitingParticipantViewHolder>() {

    private var list = listOf<Participant.Response>()

    fun setData(list: List<Participant.Response>) {
        this.list = list
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitingParticipantViewHolder {
        val binding = ItemInvitingParticipantBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return InvitingParticipantViewHolder(binding,lifecycleOwner)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: InvitingParticipantViewHolder, position: Int) {
        holder.bind(list[position])
    }
}


class InvitingParticipantViewHolder(
        private val itemBinding: ItemInvitingParticipantBinding,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(participant: Participant.Response) {
        itemBinding.participant = participant
        itemBinding.lifecycleOwner = lifecycleOwner
    }
}