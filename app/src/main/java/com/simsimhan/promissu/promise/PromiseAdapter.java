package com.simsimhan.promissu.promise;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.model.Promise;
import com.simsimhan.promissu.util.NavigationUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PromiseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Promise.Response> items;
    private AppCompatActivity appCompatActivity;
    private boolean isPastPromise;
    private static final int VIEW_TYPE_PROFILE = 1;
    private static final int VIEW_TYPE_ITEM = 2;

    public PromiseAdapter(AppCompatActivity appCompatActivity, List<Promise.Response> items, boolean isPastPromise) {
        this.appCompatActivity = appCompatActivity;
        this.items = items;
        this.isPastPromise = isPastPromise;
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
            ((ItemViewHolder )holder).setItem(items.get(position), isPastPromise, appCompatActivity);
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
        private ConstraintLayout container;
        private TextView dateLeft, title, dateLeftLabel;
        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            dateLeft = itemView.findViewById(R.id.item_time);
            title = itemView.findViewById(R.id.item_name);
            dateLeftLabel = itemView.findViewById(R.id.item_time_label);
        }

        void setItem(Promise.Response response, boolean isPastPromise, AppCompatActivity appCompatActivity) {
            title.setText(response.getDescription());

            if (isPastPromise) {
                container.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.past_background_color));
                dateLeft.setVisibility(View.GONE);

                //TODO: should use sonething else
                dateLeftLabel.setText("" + (response.getStartTime().getMonth() + 1) + "." + (response.getStartTime().getDay() + 1));
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationUtil.openPromiseDetilScreen(appCompatActivity);
                    }
                });
            } else {
                container.setBackgroundColor(ContextCompat.getColor(container.getContext(), R.color.background_grey));
                dateLeft.setVisibility(View.VISIBLE);
                dateLeft.setText("" + response.getStartTime().getDay());
                dateLeftLabel.setText("일 남음");
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (response.getStatus() == 0) {
                            NavigationUtil.openPendingScreen(appCompatActivity, response);
                        } else {
                            NavigationUtil.openPromiseDetilScreen(appCompatActivity);
                        }
                    }
                });
            }

        }
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
