package com.taxibookingdriver.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taxibookingdriver.Adapter.Chat_screen_adapter;
import com.taxibookingdriver.Controller.Pref_Master;
import com.taxibookingdriver.Models.Chat;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import java.util.ArrayList;

/**
 * Created by Admin on 3/30/2018.
 */

public class Message_confirm extends Fragment {

    Context context;
    RecyclerView chat;
    Chat_screen_adapter adapter;
    ArrayList<Chat> array_chat = new ArrayList<>();
    String tripid = "";
    String riderid = "";
    Pref_Master pref;
    RelativeLayout send_button;
    EditText chatmsg;
    String receiver = "";
    String sender = "";
    String rpic = "";


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        context = getActivity();
        pref = new Pref_Master(context);


        rpic = pref.getRider_pic();
        tripid = pref.getTripid();
        riderid = pref.getRiderid();

        chat = view.findViewById(R.id.chat);
        send_button = view.findViewById(R.id.send_button);
        chatmsg = view.findViewById(R.id.chatmsg);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chat.setLayoutManager(layoutManager);

        adapter = new Chat_screen_adapter(context, array_chat, pref);
        chat.setAdapter(adapter);

        getMessage();

        chatmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith(" ")) {
                    String result = s.toString().replaceAll(" ", "");
                    if (!s.toString().equals(result)) {
                        chatmsg.setText(result);
                        chatmsg.setSelection(result.length());
// alert the user
                    }
                }


            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = chatmsg.getText().toString();
                if (chatmsg.getText().toString().trim().length() == 0) {

                } else if (text.startsWith(" ")) {
                    chatmsg.setText(text.trim());
                } else {
                    sendMessage();
                    chatmsg.setText("");
                }

            }
        });


        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView("Message Confirm");
    }

    private void sendMessage() {
        array_chat.clear();
        String message = chatmsg.getText().toString();
        String receiver = riderid;
        String sender = pref.getUID();
        Chat chat = new Chat(sender,
                receiver,
                message,
                System.currentTimeMillis());


        String key = FirebaseDatabase.getInstance().getReference("cars").child("trip").child(tripid).child("msg").push().getKey();
        FirebaseDatabase.getInstance().getReference("cars").child("trip").child(tripid).child("msg").child(String.valueOf(key)).setValue(chat);
        chatmsg.setText("");

        /*chatRecyclerAdapter.notifyDataSetChanged();
*/

    }


    private void getMessage() {
        FirebaseDatabase.getInstance().getReference("cars").child("trip").child(tripid).child("msg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                array_chat.clear();
                for (DataSnapshot chilSnapshot : dataSnapshot.getChildren()) {
                    chat.scrollToPosition(array_chat.size());
                    Log.e("Valueee", "" + chilSnapshot.child("message").getValue());

                    Chat chat = new Chat();
                    chat.setMessage(String.valueOf(chilSnapshot.child("message").getValue()));
                    chat.setSender(String.valueOf(chilSnapshot.child("sender").getValue()));
                    chat.setReceiver(String.valueOf(chilSnapshot.child("receiver").getValue()));
                    chat.setTimestamp((Long) chilSnapshot.child("timestamp").getValue());
                    chat.setRiderid(riderid);
                    chat.setRpic(rpic);
                    array_chat.add(chat);
                    adapter.notifyDataSetChanged();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
