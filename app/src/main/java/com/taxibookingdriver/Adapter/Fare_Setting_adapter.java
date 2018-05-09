package com.taxibookingdriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Models.Model_fare;
import com.taxibookingdriver.R;

import java.util.ArrayList;

/**
 * Created by Admin on 1/16/2018.
 */

public class Fare_Setting_adapter extends RecyclerView.Adapter<Fare_Setting_adapter.Viewholder> {
    Context context;
    ArrayList<Model_fare> array_list = new ArrayList<>();

    public Fare_Setting_adapter(Context context, ArrayList<Model_fare> array_list) {
        this.context = context;
        this.array_list = array_list;

    }

    @Override
    public Fare_Setting_adapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_fare_setting_list, parent, false);
        return new Viewholder(itemView);


    }

    @Override
    public void onBindViewHolder(Fare_Setting_adapter.Viewholder holder, int position) {

        final Model_fare model_far = array_list.get(position);

        holder.txt_date.setText(model_far.getDate());
        holder.txt_time.setText(model_far.getTime());
        holder.txt_rate.setText(Constant.CURRENCY  + " " + model_far.getRate());

    }

    @Override
    public int getItemCount() {
        return array_list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_date, txt_time, txt_rate;


        public Viewholder(View view) {
            super(view);


            txt_date = view.findViewById(R.id.txt_date);
            txt_time = view.findViewById(R.id.txt_time);
            txt_rate = view.findViewById(R.id.txt_rate);
        }
    }
}
