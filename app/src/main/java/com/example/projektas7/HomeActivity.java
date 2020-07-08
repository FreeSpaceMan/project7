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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.projektas7.LoginActivity.USERNAME;

import static com.mapbox.turf.TurfConstants.UNIT_DEGREES;
import static com.mapbox.turf.TurfConstants.UNIT_KILOMETERS;
import static com.mapbox.turf.TurfConstants.UNIT_MILES;
import static com.mapbox.turf.TurfConstants.UNIT_RADIANS;


public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btnGoToMap;

    private TextView textView_SeekBar;
    private SeekBar seekBar;
    private Spinner spinner;


    private static final int RADIUS_SEEKBAR_DIFFERENCE = 1;
    private static final int RADIUS_SEEKBAR_MAX = 200;


//
    private SharedPreferences sharedPref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textView_SeekBar = (TextView) findViewById(R.id.circle_radius_textview);
        seekBar = (SeekBar) findViewById(R.id.circle_radius_seekbar);
        seekBar.setMax(RADIUS_SEEKBAR_MAX + RADIUS_SEEKBAR_DIFFERENCE);
        seekBar.incrementProgressBy(RADIUS_SEEKBAR_DIFFERENCE);
        seekBar.setProgress(RADIUS_SEEKBAR_MAX / 2);

        Spinner spinner = findViewById(R.id.circle_units_spinner);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setProgress(progress);
                Coordinates.selectedRadius = progress;
                textView_SeekBar.setText(String.format(getString(R.string.polygon_circle_transformation_circle_radius)) + progress+" "+Coordinates.selectedUnits );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Not using this
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);






        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);


        btnGoToMap = (Button)findViewById(R.id.button_goToMap);
        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapActivity();

            }
        });






    }

    public void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = "Selected units: " + parent.getItemAtPosition(position).toString();
        Coordinates.selectedUnits = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}