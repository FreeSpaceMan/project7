package com.example.projektas7;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.projektas7.LoginActivity.USERNAME;

import static com.mapbox.turf.TurfConstants.UNIT_DEGREES;
import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;
import static com.mapbox.turf.TurfConstants.UNIT_MILES;
import static com.mapbox.turf.TurfConstants.UNIT_RADIANS;


public class HomeActivity extends AppCompatActivity {
    private Button btnGoToMap;



//
    private SharedPreferences sharedPref;


    //pirmadienio kodas zemiau
    private static final int RADIUS_SEEKBAR_DIFFERENCE = 1;//pirmadienio kodas
    private static final int RADIUS_SEEKBAR_MAX = 500;//pirmadienio kodas

    // Not static final because they will be adjusted by the seekbars and spinner menu
    private String circleUnit = UNIT_KILOMETERS;
    private int circleRadius = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);





        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);


        btnGoToMap = (Button)findViewById(R.id.button_goToMap);
        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();
            }
        });



        final SeekBar circleRadiusSeekbar = findViewById(R.id.circle_radius_seekbar);
        circleRadiusSeekbar.setMax(RADIUS_SEEKBAR_MAX + RADIUS_SEEKBAR_DIFFERENCE);
        circleRadiusSeekbar.incrementProgressBy(RADIUS_SEEKBAR_DIFFERENCE);
        circleRadiusSeekbar.setProgress(RADIUS_SEEKBAR_MAX / 2);



        final TextView circleRadiusTextView = findViewById(R.id.circle_radius_textview);
        circleRadiusTextView.setText(String.format(getString(
                R.string.polygon_circle_transformation_circle_radius),
                circleRadiusSeekbar.getProgress()));


    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }




}