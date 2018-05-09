package com.taxibookingdriver.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.taxibookingdriver.Fragments.Fare_confirm;
import com.taxibookingdriver.Fragments.Message_confirm;
import com.taxibookingdriver.MyApplication;
import com.taxibookingdriver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2/1/2018.
 */

public class ConfirmTripActivity_new extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewpager;
    ViewPagerAdapter adapter;
    Context context = this;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_trip);


        viewpager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);

        createTabIcons();

        MyApplication.getInstance().trackScreenView("Confirm Trip Screen");
    }

    @Override
    public void onBackPressed() {
    }

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        tabOne.setText("Fare");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Message");
        tabLayout.getTabAt(1).setCustomView(tabTwo);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fare_confirm(), "Fare");
        adapter.addFragment(new Message_confirm(), "Message");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
