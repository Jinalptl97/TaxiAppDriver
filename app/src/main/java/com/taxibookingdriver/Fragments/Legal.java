package com.taxibookingdriver.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taxibookingdriver.Activities.MainActivity;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

/**
 * Created by Admin on 1/8/2018.
 */

public class Legal extends Fragment {

    Context context;
    TextView txt_legal;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_legal, container, false);
        context = getActivity();

        txt_legal = view.findViewById(R.id.txt_legal);

        return view;

    }

    @Override
    public void onResume() {

        super.onResume();

        if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_BACK) {

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

//                    FragmentManager fm = getFragmentManager();
//                    FragmentTransaction ft = fm.beginTransaction();
//                    ft.remove(Subscription.this);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
//                    ft.commit();
// handle back button's click listener
                }

                return false;
            }
        });
        MyApplication.getInstance().trackScreenView("Legal");

    }

}
