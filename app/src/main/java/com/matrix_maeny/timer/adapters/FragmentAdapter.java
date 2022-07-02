package com.matrix_maeny.timer.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.matrix_maeny.timer.fragments.StopWatchFragment;
import com.matrix_maeny.timer.fragments.TimerFragment;

import java.util.Timer;

public class FragmentAdapter extends FragmentStateAdapter {

    private final String[] titles = {"STOPWATCH","TIMER"}; // for titles

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    } // matching constuctor

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new StopWatchFragment(); // returning new fragments
        }
       return new TimerFragment();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    } // returning length
}
/*
*  viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        fragmentAdapters = new FragmentAdapters(this);   // creating a new object for fragmentAdapter class
        viewPager.setAdapter(fragmentAdapters);                       // setting the adapter for the viewPager

        // creating a tabLayout mediator for exchanging tabs from one to another
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout,viewPager,(tab, position) -> tab.setText(titles[position]));
        tabLayoutMediator.attach(); // attaching to the tablelayout..

* */