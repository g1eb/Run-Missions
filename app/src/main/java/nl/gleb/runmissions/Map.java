package nl.gleb.runmissions;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
    static Polyline route;
    static PolylineOptions routeOptions;
    static int animationDuration = 2000;

    Comm comm;
    Resources res;
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
        res = getResources();
        animationDuration = res.getInteger(R.integer.map_animation_duration);
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
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), animationDuration, null);

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
        final Location location = ((Main) getActivity()).mCurrentLocation;

        GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
        url.put("key", "AIzaSyCbFFLGTKvJh_on6sRgwp0mcz0Rl-B_ijk");
        url.put("location", location.getLatitude() + "," + location.getLongitude());
        url.put("radius", 5000);

        HttpRequest request;
        try {
            request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();

            final PlacesList result = httpResponse.parseAs(PlacesList.class);
            List<Place> places = result.results;

            if (map != null) {

                for (Place place : places) {
                    Marker marker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(place.geometry.location.lat, place.geometry.location.lng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .title(place.name));
                    place.setMarkerId(marker.getId());
                }
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if (route != null) {
                            route.remove(); // Remove previous route from the map if it exists
                        }

                        Place place = result.getPlace(marker.getId());
                        new DirectionsFetcher(location, place).execute();

                        return false;
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addPolyLine(List<LatLng> points) {
        if (map != null) {
            routeOptions = new PolylineOptions()
                    .visible(true)
                    .color(Color.GRAY)
                    .width(5)
                    .zIndex(30);
            routeOptions.addAll(points);
            route = map.addPolyline(routeOptions);
        }
    }
}