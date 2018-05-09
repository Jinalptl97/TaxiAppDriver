package com.taxibookingdriver.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Fragments.Timewise_trip;
import com.taxibookingdriver.Fragments.Trip_Detail;
import com.taxibookingdriver.Models.Model_earning_load;
import com.taxibookingdriver.R;

import java.util.ArrayList;

/**
 * Created by Admin on 1/16/2018.
 */

public class Timewise_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;
    Context context;
    ArrayList<Model_earning_load> array_list = new ArrayList<>();
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    FragmentManager manager;

    public Timewise_adapter(Context context, ArrayList<Model_earning_load> array_list, FragmentManager manager) {
        this.context = context;
        this.array_list = array_list;
        this.manager = manager;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_MOVIE) {
            return new Viewholder(inflater.inflate(R.layout.row_time_wise_list, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.loaderview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_MOVIE) {
            ((Viewholder) holder).bindData(array_list.get(position));
        }


    }

    @Override
    public int getItemCount() {
        return array_list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (array_list.get(position).type.equals("load")) {
            return TYPE_LOAD;
        } else {
            return TYPE_MOVIE;
        }
    }


    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_time_total, txt_time;
        RelativeLayout rr_time;

        public Viewholder(View view) {
            super(view);

            txt_time_total = view.findViewById(R.id.txt_time_total);
            txt_time = view.findViewById(R.id.txt_time);
            rr_time = view.findViewById(R.id.rr_time);
        }

        void bindData(final Model_earning_load model) {
            txt_time_total.setText(Constant.CURRENCY  + model.getAmt());
            txt_time.setText(model.getTriptime());
            rr_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Timewise_trip.riderid = model.getRiderid();
                    Timewise_trip.tripid = model.getTripid();

                    Fragment newFragment = new Trip_Detail();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.frame, newFragment);
                    transaction.addToBackStack("TripD");
                    transaction.commit();
                }
            });

        }
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}
