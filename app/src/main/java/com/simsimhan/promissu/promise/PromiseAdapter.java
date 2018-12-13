package com.simsimhan.promissu.promise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.model.Promise;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class PromiseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Promise.Response> items;
    private AppCompatActivity appCompatActivity;
    private static final int VIEW_TYPE_PROFILE = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    public PromiseAdapter(AppCompatActivity appCompatActivity, List<Promise.Response> items) {
        this.appCompatActivity = appCompatActivity;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PROFILE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_promise_row_profile, parent, false);
            return new ProfileViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_promise_row_item, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder )holder).setItem(items.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_ITEM;
//        if (items.size() == 0) {
//            return VIEW_TYPE_PROFILE;
//        } else {
//            if (position == 0) {
//                return VIEW_TYPE_PROFILE;
//            } else {
//
//            }
//        }
    }

    @Override
    public int getItemCount() {
//        return items.size() == 0 ? 1 : items.size();
        return items.size();
    }

    public void reset(List<Promise.Response> onNext) {
        items.clear();
        items.addAll(onNext);
        notifyDataSetChanged();
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView dateLeft, title;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            dateLeft = itemView.findViewById(R.id.item_time);
            title = itemView.findViewById(R.id.item_name);
        }

        void setItem(Promise.Response response) {
            title.setText(response.getName());
            dateLeft.setText("" + response.getTime().getDay());

        }
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
