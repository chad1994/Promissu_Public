package com.simsimhan.promissu.promise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    }

    @Override
    public int getItemViewType(int position) {
        if (items.size() == 0) {
            return VIEW_TYPE_PROFILE;
        } else {
            if (position == 0) {
                return VIEW_TYPE_PROFILE;
            } else {
                return VIEW_TYPE_ITEM;
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size() == 0 ? 1 : items.size();
    }


    static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
