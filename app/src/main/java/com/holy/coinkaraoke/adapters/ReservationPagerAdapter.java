package com.holy.coinkaraoke.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.holy.coinkaraoke.CategoryFragment;
import com.holy.coinkaraoke.CoinFragment;
import com.holy.coinkaraoke.PollFragment;
import com.holy.coinkaraoke.RoomFragment;


public class ReservationPagerAdapter extends FragmentStateAdapter {

    private final Fragment[] fragments = new Fragment[] {
            new RoomFragment(),
            new CoinFragment(),
            new CategoryFragment(),
            new PollFragment()
    };
    private int mMaxFragment;

    public ReservationPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

        mMaxFragment = fragments.length;
    }

    public ReservationPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                                   int maxFragment) {
        super(fragmentManager, lifecycle);

        mMaxFragment = maxFragment;
        if (mMaxFragment > fragments.length) {
            mMaxFragment = fragments.length;
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return mMaxFragment;
    }

    public Fragment getFragmentAt(int position) {
        return fragments[position];
    }
}
