package com.example.projektas7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.gson.internal.$Gson$Preconditions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;

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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapView mapView = null;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private TextView textView,textViewMessages;
    private EditText messagingInput;
    private Button btnChatSend;
    private ArrayList<UserMessages> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,getString(R.string.access_token));
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                MarkerOptions options = new MarkerOptions();
                options.title("Current position");
                options.position(new LatLng(Coordinates.latitude+1,Coordinates.longitude+1));
                mapboxMap.addMarker(options);
            }
        });





        textViewMessages = (TextView)findViewById(R.id.check_sql);
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

//        Call<JSONResponse> call = apiService.getMePosts();
//        call.enqueue(new Callback<JSONResponse>() {
//            @Override
//            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
//                JSONResponse jsonResponse = response.body();
//                data = new ArrayList<>(Arrays.asList(jsonResponse.getMessageFromList()));
//            }
//            @Override
//            public void onFailure(Call<JSONResponse> call, Throwable t) {
//                Log.d("Error", t.getMessage());
//            }
//        });

        Call<List<UserMessages>> call = apiService.getMePosts();
        call.enqueue(new Callback<List<UserMessages>>() {
            @Override
            public void onResponse(Call<List<UserMessages>> call, Response<List<UserMessages>> response) {
                if (!response.isSuccessful()){
                    textViewMessages.setText("Code: " + response.code());
                    return;
                }

                List<UserMessages> userMessages = response.body();

                for (UserMessages uMessage : userMessages){
                    String content = "";
                    content += "Username: " + uMessage.getUsername() + "\n";
                    content += "Message: " + uMessage.getMessage()+ "\n";

                    textViewMessages.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<UserMessages>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });


    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {






        map = mapboxMap;
        enableLocation();

        Coordinates.latitude = locationEngine.getLastLocation().getLatitude();
        Coordinates.longitude = locationEngine.getLastLocation().getLongitude();

        String latitudeTxt = String.valueOf(Coordinates.latitude);
        String longitudeTxt = String.valueOf(Coordinates.longitude);


        textView.setText(String.format(getString(R.string.new_location),
                latitudeTxt,
                longitudeTxt));
        Toast.makeText(this, String.format(getString(R.string.new_location),
                latitudeTxt,
                longitudeTxt),
                Toast.LENGTH_SHORT).show();

    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        locationEngine = LocationEngineFactory.getLocationEngine(this);

//        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
//        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
//        locationEngine.activate();
//        Location lastLocation = locationEngine.getLastLocation();
//        if(lastLocation != null) {
//            originLocation = lastLocation;
//            setCameraPosition(lastLocation);
//        } else {
//            locationEngine.addLocationEngineListener(this);
//        }

        setLocationProperties();
    }

    public void setLocationProperties() {
        Location lastLocation = locationEngine.getLastLocation();
        if(lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void initializeLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()),13));
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.removeLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,"Por favor! Enable the location!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
            if (locationLayerPlugin != null) {
                locationLayerPlugin.onStart();
            }
        }
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
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStop();
        }
        mapView.onStop();;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }




}
