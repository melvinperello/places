package com.melvinperello.places.domain;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.melvinperello.places.R;

import java.util.List;

//--------------------------------------------------------------------------------------------------
// 01. Create a class that will represent the data in the card view.
//--------------------------------------------------------------------------------------------------
public class PlacesListItem {

    //----------------------------------------------------------------------------------------------
    // 02. Data that you will display.
    //----------------------------------------------------------------------------------------------
    private String date;
    private String name;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    //----------------------------------------------------------------------------------------------
    // 03. Data Adapter.
    //----------------------------------------------------------------------------------------------
    public static class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        /**
         * Data.
         */
        private final List<PlacesListItem> data;

        public DataAdapter(@NonNull List<PlacesListItem> data) {
            this.data = data;
        }

        /**
         * Inflate the View Holder with the XML Layout.
         *
         * @param viewGroup
         * @param i
         * @return
         */
        @NonNull
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // inflate the view using the view group context
            // do not attach to root [last false parameter]
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_item_places, viewGroup, false);
            // return as a view holder.
            return new ViewHolder(view);
        }

        /**
         * Builds the layout with the values from the list.
         *
         * @param viewHolder
         * @param i
         */
        @Override
        public void onBindViewHolder(@NonNull DataAdapter.ViewHolder viewHolder, int i) {
            PlacesListItem item = this.data.get(i); // get the item
            viewHolder.itemView.setTag(item); // assign tag to this view, retrieve later.
            // set the values
            viewHolder.tvDate.setText(item.getDate());
            viewHolder.tvName.setText(item.getName());
            viewHolder.imgIcon.setImageResource(R.drawable.img_location_64);
        }

        /**
         * Return the number of items in the list.
         *
         * @return
         */
        @Override
        public int getItemCount() {
            return this.data.size();
        }


        //----------------------------------------------------------------------------------------------
        // 04. View Holder. that card itself.
        //----------------------------------------------------------------------------------------------
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            TextView tvName;
            ImageView imgIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView); // do not remove this
                tvDate = itemView.findViewById(R.id.tvDate);
                tvName = itemView.findViewById(R.id.tvName);
                imgIcon = itemView.findViewById(R.id.imgIcon);
            }
        }
    }
}
