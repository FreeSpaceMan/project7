package com.example.projektas7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.projektas7.LoginActivity.EMAIL;
import static com.example.projektas7.LoginActivity.ID;
import static com.example.projektas7.LoginActivity.NAME;
import static com.example.projektas7.LoginActivity.SURNAME;
import static com.example.projektas7.LoginActivity.USERNAME;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {




    private MapView mapView;

    private MapboxMap mapboxMap;

    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;

    private TextView textView,textViewMessages;
    private EditText messagingInput;
    private Button btnChatSend;




    ArrayList<UserMessages> usersMessages = new ArrayList<>();
    private UserAdapter userAdapter;
    private RecyclerView userMessages_recyclerview;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this,getString(R.string.access_token));

        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        userMessages_recyclerview=(RecyclerView)findViewById(R.id.streaming_list);
        userMessages_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        btnChatSend = (Button)findViewById(R.id.button_chatbox_send);

        SharedPreferences sharedPref = getSharedPreferences("UserData", MODE_PRIVATE);

        messagingInput=(EditText)findViewById(R.id.messaging_input);
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        ApiService apiService = retrofitHelper.retrofitHelp().create(ApiService.class);

        btnChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageSent = messagingInput.getText().toString();
                String userIdX = sharedPref.getString(ID,"");
                String usernameX = sharedPref.getString(USERNAME,"");

                if(messageSent.equals("")){
                    Toast.makeText(MapActivity.this,"Please insert a message", Toast.LENGTH_SHORT).show();
                }else{

                    apiService.insertMessage(""+messageSent,
                            ""+userIdX,
                            ""+usernameX,
                            ""+ Coordinates.latitude,
                            ""+ Coordinates.longitude).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d("MapActivity","SUCCESS");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("MapActivity","FAILURE");
                            t.printStackTrace();
                        }
                    });
                }

            }
        });

        Call<List<UserMessages>> call = apiService.getMePosts();

        call.enqueue(new Callback<List<UserMessages>>() {
            @Override
            public void onResponse(Call<List<UserMessages>> call, Response<List<UserMessages>> response) {
                usersMessages = new ArrayList<>(response.body());
                userAdapter = new UserAdapter(MapActivity.this,usersMessages);
                userMessages_recyclerview.setAdapter(userAdapter);
            }
            @Override
            public void onFailure(Call<List<UserMessages>> call, Throwable t) {
                Log.d("Error", t.getMessage());
                Toast.makeText(MapActivity.this,"Failed to load messages",Toast.LENGTH_SHORT).show();

            }
        });



    }

    //****************************************************************************************************************************************************
    //****************************************************************************************************************************************************
    // NEW MAPBOX CODE BELOW!*****************************************************************************************************************************
    //****************************************************************************************************************************************************
    //****************************************************************************************************************************************************

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapActivity.this.mapboxMap = mapboxMap;
//fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/satellite-v9"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            if (locationComponent.getLastKnownLocation() != null) {
                Coordinates.latitude = locationComponent.getLastKnownLocation().getLatitude();
                Coordinates.longitude = locationComponent.getLastKnownLocation().getLongitude();
                    Toast.makeText(this, String.format(getString(R.string.new_location),
                            String.valueOf(locationComponent.getLastKnownLocation().getLatitude()),
                            String.valueOf(locationComponent.getLastKnownLocation().getLongitude())), Toast.LENGTH_LONG).show();
            }



        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }




}
