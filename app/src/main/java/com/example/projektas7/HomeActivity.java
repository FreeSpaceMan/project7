package com.example.projektas7;

import android.os.Bundle;

import com.example.projektas7.fragments.FirstFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirstFragment firstFragment = new FirstFragment();
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction().add(R.id.homeActivityLayout,firstFragment).commit();

    }
}