package com.taxibookingdriver.Adapter;

import android.content.Context;
import android.os.Bundle;
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
import com.taxibookingdriver.Models.Model_earning_load;
import com.taxibookingdriver.R;

import java.util.ArrayList;

/**
 * Created by Admin on 1/16/2018.
 */

public class Datewise_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final int TYPE_MOVIE = 0;
    public final int TYPE_LOAD = 1;
    Context context;
    ArrayList<Model_earning_load> array_list = new ArrayList<>();
    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;
    FragmentManager manager;


    public Datewise_adapter(Context context, ArrayList<Model_earning_load> array_list, FragmentManager manager) {
        this.array_list = array_list;
        this.context = context;
        this.manager = manager;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_MOVIE) {
            return new Viewholder(inflater.inflate(R.layout.row_date_wise_list, parent, false));
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


        TextView txt_per_balance, txt_per_date;
        RelativeLayout rr_date_wise;

        public Viewholder(View view) {
            super(view);

            txt_per_balance = view.findViewById(R.id.txt_per_balance);
            txt_per_date = view.findViewById(R.id.txt_per_date);
            rr_date_wise = view.findViewById(R.id.rr_date_wise);


        }

        void bindData(final Model_earning_load model) {
            txt_per_date.setText(model.getTripdate());
            txt_per_balance.setText(Constant.CURRENCY  + model.getAmt());
            rr_date_wise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment newFragment = new Timewise_trip();
                    Bundle data = new Bundle();
                    data.putString("tripday", model.getTripday());
                    data.putString("tripdate", model.getTripdate());
                    FragmentTransaction transaction = manager.beginTransaction();
                    newFragment.setArguments(data);
                    transaction.replace(R.id.frame, newFragment);
                    transaction.addToBackStack("TimeWise");
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
