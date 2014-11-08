package nl.gleb.runmissions;

import android.location.Location;
import android.os.AsyncTask;

import com.google.api.client.http.GenericUrl;

import java.net.URL;

/**
 * Created by Gleb on 08/11/14.
 */
public class DirectionsFetcher extends AsyncTask<URL, Integer, String> {

    Location origin;
    Place target;

    public DirectionsFetcher(Location origin, Place target) {
        this.origin = origin;
        this.target = target;
    }

    @Override
    protected String doInBackground(URL... params) {
        GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
        url.put("origin", origin.getLatitude() + "," + origin.getLongitude());
        url.put("destination", target.geometry.location.lat + "," + target.geometry.location.lng);
        url.put("sensor",false);
        return null;
    }

    public void startNavigation() {
    }
}
