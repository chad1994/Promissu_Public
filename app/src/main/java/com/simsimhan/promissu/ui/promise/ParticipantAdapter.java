//package com.simsimhan.promissu.ui.promise;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.simsimhan.promissu.R;
//import com.simsimhan.promissu.network.model.Participant;
//
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class ParticipantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    private List<Participant.Response> list;
//    private static final int VIEW_TYPE_PARTICIPANT = 1;
//
//    public ParticipantAdapter(List<Participant.Response> list) {
//        this.list = list;
//    }
//
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_participant_row_item, parent, false);
//        return new PromiseAdapter.ItemViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof ParticipantAdapter.ParticipantViewHolder) {
//            ((ParticipantAdapter.ParticipantViewHolder) holder).setItem(list.get(position));
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return VIEW_TYPE_PARTICIPANT;
//    }
//
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public void addAll(List<Participant.Response> onNext) {
//        list.clear();
//        list.addAll(onNext);
//        notifyDataSetChanged();
//    }
//
//
//    static class ParticipantViewHolder extends RecyclerView.ViewHolder {
//        private TextView itemName, itemId;
//
//        public ParticipantViewHolder(@NonNull View itemView) {
//            super(itemView);
//            itemName = itemView.findViewById(R.id.item_name);
//            itemId = itemView.findViewById(R.id.item_id);
//        }
//
//        public void setItem(Participant.Response response) {
//            itemId.setText(response.getKakao_id());
//            itemName.setText(response.getNickname());
//        }
//    }
//
//}
