package edu.uiuc.cs427app;

import androidx.fragment.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import edu.uiuc.cs427app.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    String cityname;
    double latitude = 40.11642; //Champaign
    double longitude = -88.24338;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //process the city information in the intent package
        Intent intent = getIntent();
        cityname = intent.getStringExtra("city");
        latitude = intent.getDoubleExtra("lat",-34);
        longitude = intent.getDoubleExtra("lon",151);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used. We add a marker with the cityname
     * and move the map view camera to the city's latitude and longitude coords. Map preferences are
     * saved in activity_maps.xml
     * @param googleMap  the map to be drawn
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng citylocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(citylocation).title(cityname));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(citylocation));
    }
}