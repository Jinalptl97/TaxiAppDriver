package com.taxibookingdriver.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Models.Chat;
import com.taxibookingdriver.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 1/18/2018.
 */

public class Chat_screen_adapter extends RecyclerView.Adapter<Chat_screen_adapter.ViewHolder> {

    Context context;
    ArrayList<Chat> arrayList = new ArrayList<>();
    ViewHolder holder;
    Pref_Master pref;


    public Chat_screen_adapter(Context context, ArrayList<Chat> array_chat, Pref_Master pref) {

        this.arrayList = array_chat;
        this.context = context;
        this.pref = pref;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_chat_list, parent, false);


        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (arrayList.get(position).getSender().equals(pref.getUID())) {
            holder.sender.setText(arrayList.get(position).getMessage());

            Log.e("My_PRofile", pref.getBucket_url() + pref.getUID() + "/" + "Profile" + "/" + pref.getBack_img());

            Glide.with(context)
                    .load(pref.getBucket_url() + pref.getUID() + "/" + "Profile" + "/" + pref.getBack_img()) //extract as User instance method
                    .placeholder(R.drawable.personal)
                    .into(holder.user_sender_pic);


            holder.ll_sender.setVisibility(View.VISIBLE);
            holder.ll_receiver.setVisibility(View.GONE);
        } else {
            holder.receiver.setText(arrayList.get(position).getMessage());


            Glide.with(context)
                    .load(pref.getBucket_url() + arrayList.get(position).getRiderid() + "/" + arrayList.get(position).getRpic()) //extract as User instance method
                    .placeholder(R.drawable.personal)
                    .into(holder.user_receiver_profile);


            holder.ll_sender.setVisibility(View.GONE);
            holder.ll_receiver.setVisibility(View.VISIBLE);
        }


    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_receiver, ll_sender;
        TextView sender, receiver;
        CircleImageView user_sender_pic, user_receiver_profile;


        public ViewHolder(View itemView) {
            super(itemView);

            ll_receiver = itemView.findViewById(R.id.ll_receiver);
            ll_sender = itemView.findViewById(R.id.ll_sender);
            sender = itemView.findViewById(R.id.sender);
            receiver = itemView.findViewById(R.id.receiver);
            user_sender_pic = itemView.findViewById(R.id.user_sender_pic);
            user_receiver_profile = itemView.findViewById(R.id.user_receiver_profile);

        }
    }
}
