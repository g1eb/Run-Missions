package nl.gleb.runmissions;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gleb on 29/10/14.
 */
public class Map extends SupportMapFragment implements GoogleMap.OnMapLoadedCallback, GoogleMap.OnCameraChangeListener {

    public static GoogleMap map;
    static Polyline route;
    static PolylineOptions routeOptions;

    private static HashMap<String, Place> placesMarkers = new HashMap<String, Place>();
    private HashMap<String, Marker> usersMarkers = new HashMap<String, Marker>();

    static Comm comm;
    Resources res;
    static Location location;
    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();

    HttpRequestFactory requestFactory;

    /* Animations */
    static int mapAnimationDuration = 2000;
    static long markerAnimationDuration = 300;
    static final Handler handler = new Handler();

    public static Map newInstance() {
        return new Map();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_map));
        res = getResources();
        mapAnimationDuration = res.getInteger(R.integer.map_animation_duration);
        markerAnimationDuration = res.getInteger(R.integer.marker_animation_duration);
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

    @Override
    public void onMapLoaded() {
        setupMap();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
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
                map.setOnCameraChangeListener(this);
                map.setBuildingsEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    }

    private void setupMap() {
        // Center the map on the current position of the user
        location = ((Main) getActivity()).mCurrentLocation;
        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(center)
                .zoom(res.getInteger(R.integer.map_zoom_level))
                .tilt(res.getInteger(R.integer.map_tilt_level))
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), mapAnimationDuration, null);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (route != null) {
                    route.remove(); // Remove previous route from the map if it exists
                }

                Place target = placesMarkers.get(marker.getId());
                if (target != null) {
                    comm.setTarget(target);
                    new DirectionsFetcher(((Main) getActivity()).mCurrentLocation, target).execute();
                }

                return false;
            }
        });

        getPlaces();
    }

    public void getPlaces() {
        new PlacesFetcher(((Main) getActivity())).execute();
    }

    public static void updatePlaceMarker(Place place) {
        if (placesMarkers.containsKey(place.markerId)) {
            placesMarkers.remove(place.markerId);
            addPlaceMarker(place);
        } else {
            addPlaceMarker(place);
        }
    }

    private static void addPlaceMarker(Place place) {
        final LatLng target = new LatLng(place.geometry.location.lat, place.geometry.location.lng);

        final Marker marker = Map.map.addMarker(new MarkerOptions()
                .position(target)
                .anchor((float) 0.5, (float) 0.5)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.cross))
                .title(place.name));

        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();

        Point startPoint = proj.toScreenLocation(target);
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / markerAnimationDuration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 10);
                } else {
                    marker.setPosition(target);
                }
            }
        });

        place.setMarkerId(marker.getId());
        placesMarkers.put(marker.getId(), place);
    }

    public static void addPolyLine(List<LatLng> points) {
        if (map != null) {
            routeOptions = new PolylineOptions()
                    .visible(true)
                    .color(new Color().parseColor("#c21d2b"))
                    .width(15)
                    .zIndex(30);
            routeOptions.addAll(points);
            route = map.addPolyline(routeOptions);
        }
    }

    public static void setBounds(DirectionsBounds directionsBounds) {
        if (map != null) {
            LatLngBounds bounds = new LatLngBounds(
                    new LatLng(directionsBounds.southwest.lat, directionsBounds.southwest.lng),
                    new LatLng(directionsBounds.northeast.lat, directionsBounds.northeast.lng));

            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250), mapAnimationDuration, null);
        }
    }

    public static void setSteps(List<DirectionsStep> steps) {
        comm.updateSteps(steps);
    }

    public void updateUsersPosition(User user) {
        if (usersMarkers.containsKey(user.getUsername())) {
            usersMarkers.get(user.getUsername()).remove();
            addUserMarker(user);
        } else {
            addUserMarker(user);
        }
    }

    private void addUserMarker(User user) {
        usersMarkers.put(user.getUsername(), map.addMarker(new MarkerOptions()
                .position(new LatLng(user.getLat(), user.getLng()))
                .anchor((float) 0.5, (float) 0.5)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_user))
                .title(user.getUsername())));
    }
}