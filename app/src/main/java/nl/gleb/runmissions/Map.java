package nl.gleb.runmissions;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by Gleb on 29/10/14.
 */
public class Map extends SupportMapFragment implements GoogleMap.OnMapLoadedCallback {

    private static GoogleMap map;
    Comm comm;
    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();

    HttpRequestFactory requestFactory;

    public static Map newInstance() {
        return new Map();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_map));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFactory = HTTP_TRANSPORT.createRequestFactory(
                new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                }
        );

        initMap();
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    @Override
    public void onStop() {
        super.onStop();
        map = null;
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
                map.setTrafficEnabled(false);
                map.setIndoorEnabled(true);
                map.setOnMapLoadedCallback(this);
                map.setBuildingsEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    }

    private void setupMap() {
        Resources res = getResources();

        // Center the map on the current position of the user
        Location location = ((Main) getActivity()).mCurrentLocation;
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(center)
                .zoom(res.getInteger(R.integer.map_zoom_level))
                .tilt(res.getInteger(R.integer.map_tilt_level))
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                res.getInteger(R.integer.map_animation_duration), null);

        getPlaces();
    }

    @Override
    public void onMapLoaded() {
        setupMap();
    }

    /*
     * Get places of interest around the user
     */
    public void getPlaces() {
        Location l = ((Main) getActivity()).mCurrentLocation;

        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        url.put("key", "AIzaSyCbFFLGTKvJh_on6sRgwp0mcz0Rl-B_ijk");
        url.put("location", l.getLatitude() + "," + l.getLongitude());
        url.put("radius", 5000);

        HttpRequest request;
        try {
            request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();

            PlacesList result = httpResponse.parseAs(PlacesList.class);

            List<Place> places = result.results;
            if (map != null) {

                for (Place place : places) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(place.geometry.location.lat, place.geometry.location.lng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .title(place.name)
                            .snippet("Lat: " + place.geometry.location.lat + " Lng: " + place.geometry.location.lng));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}