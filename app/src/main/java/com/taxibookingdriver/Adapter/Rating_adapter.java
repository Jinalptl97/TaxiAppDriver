package com.taxibookingdriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.taxibookingdriver.Models.Model_rating;
import com.taxibookingdriver.R;

import java.util.ArrayList;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by Admin on 1/16/2018.
 */

public class Rating_adapter extends RecyclerView.Adapter<Rating_adapter.Viewholder> {
    Context context;
    ArrayList<Model_rating> array_list = new ArrayList<>();

    public Rating_adapter(Context context, ArrayList<Model_rating> array_list) {
        this.context = context;
        this.array_list = array_list;
    }

    @Override
    public Rating_adapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rating_list, parent, false);
        return new Viewholder(itemView);
    }

    @Override
    public void onBindViewHolder(Rating_adapter.Viewholder holder, int position) {

        final Model_rating modelRating = array_list.get(position);

        holder.rating_comment.setText(modelRating.getRating_msg());
        holder.rating_date.setText(modelRating.getRating_date());
        holder.ratingbar.setRating(Float.parseFloat(modelRating.getRating()));

    }

    @Override
    public int getItemCount() {
        return array_list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView rating_comment, rating_date;
        MaterialRatingBar ratingbar;


        public Viewholder(View view) {
            super(view);

            rating_comment = view.findViewById(R.id.rating_comment);
            rating_date = view.findViewById(R.id.rating_date);
            ratingbar = view.findViewById(R.id.ratingbar);


        }
    }
}
