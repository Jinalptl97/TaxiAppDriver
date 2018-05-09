package com.taxibookingdriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxibookingdriver.Controller.Constant;
import com.taxibookingdriver.Models.Model_earning;
import com.taxibookingdriver.R;

import java.util.ArrayList;

/**
 * Created by Admin on 1/16/2018.
 */

public class Subscription_adapter extends RecyclerView.Adapter<Subscription_adapter.Viewholder> {
    Context context;
    ArrayList<Model_earning> array_list = new ArrayList<>();

    public Subscription_adapter(Context context, ArrayList<Model_earning> array_list) {
        this.context = context;
        this.array_list = array_list;

    }

    @Override
    public Subscription_adapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_subscription_list, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Subscription_adapter.Viewholder holder, int position) {

        final Model_earning model_earning = array_list.get(position);

        holder.sub_date.setText(model_earning.getFirstday() + " " + "to" + " " + model_earning.getLastday());
        holder.sub_amt.setText(Constant.CURRENCY  + model_earning.getAmt());

    }

    @Override
    public int getItemCount() {
        return array_list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView sub_date, sub_amt;

        public Viewholder(View view) {
            super(view);

            sub_date = view.findViewById(R.id.sub_date);
            sub_amt = view.findViewById(R.id.sub_amt);

        }
    }
}
