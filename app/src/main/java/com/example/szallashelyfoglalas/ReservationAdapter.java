package com.example.szallashelyfoglalas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> implements Filterable {

    private ArrayList<Reservation> mReservationData;
    private ArrayList<Reservation> mReservationDataAll;
    private Context mContext;
    private int lastPosition = -1;
    ReservationAdapter(Context context, ArrayList<Reservation> data)
    {
        this.mReservationData = data;
        this.mContext = context;
        this.mReservationDataAll = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_reservation,parent,false));
    }

    @Override
    public void onBindViewHolder(ReservationAdapter.ViewHolder holder, int position) {
        Reservation current = mReservationData.get(position);
        holder.bindTo(current);
        if(holder.getAdapterPosition()>lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mReservationData.size();
    }

    @Override
    public Filter getFilter() {
        return ReservationFilter;
    }
    private Filter ReservationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Reservation> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();
            if(charSequence == null ||charSequence.length()==0){
                results.count = mReservationDataAll.size();
                results.values = mReservationDataAll;
            }
            else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Reservation item : mReservationDataAll){
                    if(item.getRoom_hotel().toLowerCase().contains(filterPattern))
                        filteredList.add(item);
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mReservationDataAll = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };
    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mHotelText;
        private TextView mPriceText;
        private TextView mTypeText;
        private TextView mFirstday;
        private TextView mLastday;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mHotelText = itemView.findViewById(R.id.itemHotel);
            mPriceText = itemView.findViewById(R.id.itemPrice);
            mTypeText = itemView.findViewById(R.id.itemType);
            mFirstday = itemView.findViewById(R.id.firstday);
            mLastday = itemView.findViewById(R.id.lastday);
        }

        public void bindTo(Reservation current) {
            mHotelText.setText(current.getRoom_hotel());
            mPriceText.setText(String.valueOf(current.getFull_price()));
            mTypeText.setText(current.getRoom_type());
            mFirstday.setText(current.getFirstday().toString());
            mLastday.setText(current.getLastday().toString());

            itemView.findViewById(R.id.delete)
                    .setOnClickListener(view
                            -> ((Reservations) mContext)
                            .deleteItem(current));

        }

    }
}
