package com.melvinperello.places.domain;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melvinperello.places.R;

import java.util.List;

public class TravelStreamListDataAdapter extends RecyclerView.Adapter<TravelStreamListDataAdapter.ViewHolder> {

    private List<TravelStreamListData> data;
    private ItemClicked activity;

    public interface ItemClicked {
        void onItemClicked(int index);
    }

    public TravelStreamListDataAdapter(Context context, List<TravelStreamListData> data) {
        this.data = data;
        this.activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrigin;
        TextView tvDestination;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrigin = itemView.findViewById(R.id.tvOrigin);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(data.indexOf(v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public TravelStreamListDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_temp_travel_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TravelStreamListDataAdapter.ViewHolder viewHolder, int i) {
        TravelStreamListData currentData = data.get(i);
        viewHolder.itemView.setTag(currentData);
        viewHolder.tvOrigin.setText(currentData.getOrigin());
        viewHolder.tvDestination.setText(currentData.getDestination());
        viewHolder.tvTime.setText(currentData.getStartTime());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
