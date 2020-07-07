package com.example.projektas7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.BubbleLayout;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.projektas7.LoginActivity.ID;
import static com.example.projektas7.LoginActivity.USERNAME;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static java.nio.file.Paths.get;

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

    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";


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

                messagingInput.getText().clear();

                if(messageSent.equals("")){
                    Toast.makeText(MapActivity.this,"Please insert a message", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MapActivity.this,"Message sent", Toast.LENGTH_SHORT).show();
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
                Coordinates.trialList = new ArrayList<>(response.body());
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

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MapActivity.this.mapboxMap = mapboxMap;
//fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/niekselis/ckc6d1vgs15st1ip2kloy9emo"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
//                        style.addImage(MARKER_IMAGE, BitmapFactory.decodeResource(
//                                MapActivity.this.getResources(), R.drawable.custom_marker));
//                        addMarkers(style);
                    }
                });


        String markerTitleText = new String();
        String markerTitleText_1 = new String();
        double distanceCriteria;
        for (int i = 0; i<Coordinates.trialList.size();i++) {
            markerTitleText_1 = String.valueOf(Coordinates.trialList.get(i).getUsername());
             markerTitleText = markerTitleText_1 +" says: "+String.valueOf(Coordinates.trialList.get(i).getMessage());


             distanceCriteria = Math.pow(Math.pow(Coordinates.latitude-Coordinates.trialList.get(i).getLatitude(),2)+
                     Math.pow(Coordinates.longitude-Coordinates.trialList.get(i).getLongitude(),2),0.5);

            if (distanceCriteria<0.02){
        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(Coordinates.trialList.get(i).getLatitude(), Coordinates.trialList.get(i).getLongitude()))
                .title(markerTitleText));}}
//         .title(String.valueOf(Coordinates.trialList.get(i).getMessage())));}

//        mapboxMap.addMarker(new MarkerOptions()
//                .position(new LatLng(Coordinates.trialList.get(24).getLatitude(), Coordinates.trialList.get(24).getLongitude()))
//                .title(String.valueOf(Coordinates.trialList.get(24).getMessage())));
    }




    private void addMarkers(@NonNull Style loadedMapStyle) {
        List<Feature> features = new ArrayList<>();
        for (int i = 0; i<Coordinates.trialList.size();i++) {
        features.add(Feature.fromGeometry(Point.fromLngLat(Coordinates.trialList.get(i).getLongitude()+1, Coordinates.trialList.get(i).getLatitude()+1)));}
//        features.add(Feature.fromGeometry(Point.fromLngLat(Coordinates.trialList.get(1).getLongitude()+1, Coordinates.trialList.get(1).getLatitude()+1)));

        /* Source: A data source specifies the geographic coordinate where the image marker gets placed. */

        loadedMapStyle.addSource(new GeoJsonSource(MARKER_SOURCE, FeatureCollection.fromFeatures(features)));

        /* Style layer: A style layer ties together the source and image and specifies how they are displayed on the map. */
        loadedMapStyle.addLayer(new SymbolLayer(MARKER_STYLE_LAYER, MARKER_SOURCE)
                .withProperties(
                        iconAllowOverlap(true),
                        PropertyFactory.iconIgnorePlacement(true),
                        iconImage(MARKER_IMAGE),
// Adjust the second number of the Float array based on the height of your marker image.
// This is because the bottom of the marker should be anchored to the coordinate point, rather
// than the middle of the marker being the anchor point on the map.
                        iconOffset(new Float[] {0f, -52f})
                ));
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
