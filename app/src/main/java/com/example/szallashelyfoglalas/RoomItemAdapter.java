package com.example.szallashelyfoglalas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RoomItemAdapter extends RecyclerView.Adapter<RoomItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<RoomItem> mRoomItemsData;
    private ArrayList<RoomItem> mRoomItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    RoomItemAdapter(Context context, ArrayList<RoomItem> itemsData) {
        this.mRoomItemsData = itemsData;
        this.mContext = context;
        this.mRoomItemsDataAll = itemsData;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_room, parent, false));
    }

    @Override
    public void onBindViewHolder(RoomItemAdapter.ViewHolder holder, int position) {
        RoomItem currentItem = mRoomItemsData.get(position);
        holder.bindTo(currentItem);
        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mRoomItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return roomFilter;
    }

    private Filter roomFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<RoomItem> filteredList = new ArrayList<>();
            FilterResults result = new FilterResults();
            if (charSequence == null || charSequence.length() == 0) {
                result.count = mRoomItemsDataAll.size();
                result.values = mRoomItemsDataAll;
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (RoomItem item : mRoomItemsDataAll) {
                    if (item.getHotel().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                result.count = filteredList.size();
                result.values = filteredList;
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mRoomItemsData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mHotelText;
        private TextView mPriceText;
        private TextView mCityText;
        private TextView mCountryText;
        private TextView mTypeText;

        private DatePicker firstday;
        private DatePicker lastday;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mHotelText = itemView.findViewById(R.id.itemHotel);
            mPriceText = itemView.findViewById(R.id.itemPrice);
            mCityText = itemView.findViewById(R.id.itemCity);
            mCountryText = itemView.findViewById(R.id.itemCountry);
            mTypeText = itemView.findViewById(R.id.itemType);

        }

        public void bindTo(RoomItem currentItem) {
            mHotelText.setText(currentItem.getHotel());
            mPriceText.setText(String.valueOf(currentItem.getPrice()));
            mCityText.setText(currentItem.getLocation().getCity());
            mCountryText.setText(currentItem.getLocation().getCountry());
            mTypeText.setText(currentItem.getType());

            itemView.findViewById(R.id.delete)
                    .setOnClickListener(view
                            -> ((RoomList) mContext)
                            .deleteItem(currentItem));

            firstday = itemView.findViewById(R.id.firstday);
            lastday = itemView.findViewById(R.id.lastday);

            itemView
                    .findViewById(R.id.reservation)
                    .setOnClickListener(view
                            -> ((RoomList) mContext)
                            .reservation(currentItem, firstday, lastday));

        }
    }


}

