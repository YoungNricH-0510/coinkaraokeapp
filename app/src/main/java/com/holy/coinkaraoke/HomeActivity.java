package com.holy.coinkaraoke;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.holy.coinkaraoke.adapters.ReservationPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    public static final String[] TAB_TITLES = {
            "ROOM", "COIN", "SONG", "POLL",
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create pager adapter for view pager
        ReservationPagerAdapter reservationPagerAdapter;

        if (((App)getApplication()).getCurrentId() != null) {
            reservationPagerAdapter = new ReservationPagerAdapter(
                    getSupportFragmentManager(), getLifecycle());
        } else {
            reservationPagerAdapter = new ReservationPagerAdapter(
                    getSupportFragmentManager(), getLifecycle(), 1);
        }

        // Connect view pager with the adapter
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(reservationPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 1) {
                    CoinFragment coinFragment = (CoinFragment)reservationPagerAdapter.getFragmentAt(1);
                    coinFragment.updateViews();
                }
            }
        });

        // Connect view pager with tab layout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])).attach();
    }
}