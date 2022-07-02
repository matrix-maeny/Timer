package com.matrix_maeny.timer;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.matrix_maeny.timer.adapters.FragmentAdapter;
import com.matrix_maeny.timer.fragments.StopWatchFragment;
import com.matrix_maeny.timer.fragments.TimerFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager; // reference for ViewPager2
    TabLayout tabLayout; // reference for TabLayout
    private final String[] titles = {"STOPWATCH", "TIMER"}; // to store and to show titles


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide(); // hiding the toolbar


        viewPager = findViewById(R.id.viewPager); // finding the viewpager
        tabLayout = findViewById(R.id.tabLayout); // finding the tabLayout in the layout
        Toast.makeText(this, "To get accurate results DON'T close the app", Toast.LENGTH_LONG).show();


        // setting the adapter to the fragments


        FragmentAdapter adapter = new FragmentAdapter(this); // creating new adapter object
        viewPager.setAdapter(adapter); // setting the adapter to the viewPager

        //creating a tabMediator to revolve around the tabs
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> tab.setText(titles[position])));
        mediator.attach(); // attaching the mediator to the pager and tabs


    }


}