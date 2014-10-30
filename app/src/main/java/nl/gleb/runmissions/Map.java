package nl.gleb.runmissions;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Gleb on 29/10/14.
 */
public class Map extends SupportMapFragment implements GoogleMap.OnMapLoadedCallback {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static GoogleMap map;

    public static Map newInstance(int sectionNumber) {
        Map fragment = new Map();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((Main) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    private void initMap() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                map.setMyLocationEnabled(true);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                map.setIndoorEnabled(true);
                map.setOnMapLoadedCallback(this);
            }
        }
    }

    @Override
    public void onMapLoaded() {
        Location location = ((Main) getActivity()).mCurrentLocation;

//        map.addMarker(new MarkerOptions().position(new LatLng(pos.getLatitude(), pos.getLongitude())).title("You are here."));
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, getResources().getInteger(R.integer.map_zoom_level));
        map.animateCamera(cameraUpdate);

    }
}
