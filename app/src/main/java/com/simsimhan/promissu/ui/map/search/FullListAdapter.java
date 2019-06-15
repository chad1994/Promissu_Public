package com.simsimhan.promissu.ui.map.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.simsimhan.promissu.R;
import com.simsimhan.promissu.model.LocationSearchItem;

import java.util.List;

public class FullListAdapter extends BaseAdapter implements Filterable {
    private List<LocationSearchItem> documents;
    private RecommendClickListener listener;

    public void replaceAll(List<LocationSearchItem> documents) {
        this.documents = documents;
        notifyDataSetChanged();
    }

    public interface RecommendClickListener {
        void onClick(LocationSearchItem position);
    }

    public FullListAdapter(List<LocationSearchItem> documents, RecommendClickListener listener) {
        this.documents = documents;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return documents.size();
    }

    @Override
    public Object getItem(int position) {
        return documents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null && inflater != null) {
            convertView = inflater.inflate(R.layout.suggestion_list_view, parent, false);
        }

        LocationSearchItem listViewItem = documents.get(position);
        TextView name = convertView.findViewById(R.id.place_name);
        TextView location = convertView.findViewById(R.id.place_location);
        convertView.findViewById(R.id.container).setOnClickListener(v -> listener.onClick(listViewItem));
        name.setText(listViewItem.getName());
        location.setText(listViewItem.getRoad_address());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                // Assign the data to the FilterResults
                filterResults.values = documents;
                filterResults.count = documents.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }
}
