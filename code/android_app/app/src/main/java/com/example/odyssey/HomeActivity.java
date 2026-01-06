package com.example.odyssey;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.odyssey.fragments.HistoryFragment;
import com.example.odyssey.fragments.HomeFragment;
import com.example.odyssey.fragments.ProfileFragment;
import com.example.odyssey.fragments.RequestFragment;
import com.example.odyssey.fragments.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(menuItem -> {
            Fragment fragment;

            if (menuItem.getItemId() == R.id.navigation_profile) {
                fragment = new ProfileFragment();
                toggleToolbar(true, "Profile");
            } else if (menuItem.getItemId() == R.id.navigation_history) {
                fragment = new HistoryFragment();
                toggleToolbar(true, "History");
            } else if (menuItem.getItemId() == R.id.navigation_settings) {
                fragment = new SettingFragment();
                toggleToolbar(true, "Settings");
            } else if (menuItem.getItemId() == R.id.navigation_request) {
                fragment = new RequestFragment();
                toggleToolbar(true, "Booking Request");

            } else {
                fragment = new HomeFragment();
                toggleToolbar(false, null);
            }

            loadFragment(fragment);
            return true;
        });

        bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_container, fragment)
                .commit();
    }

    private void toggleToolbar(boolean show, String title) {
        if (show) {
            toolbar.setVisibility(View.VISIBLE);
            toolbarTitle.setText(title);
        } else {
            toolbar.setVisibility(View.GONE);
        }
    }
}