package nl.gleb.runmissions;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gleb on 10/11/14.
 */
public class PlacesFetcher extends AsyncTask<URL, Integer, String> {

    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();

    HttpRequestFactory requestFactory;

    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private List<DirectionsStep> steps = new ArrayList<DirectionsStep>();
    private DirectionsBounds bounds;

    Location location;
    PlacesList places;

    public PlacesFetcher(Location location) {
        this.location = location;
    }

    @Override
    protected String doInBackground(URL... params) {
        try {
            requestFactory = HTTP_TRANSPORT.createRequestFactory(
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    }
            );

            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json");
            url.put("key", "AIzaSyCbFFLGTKvJh_on6sRgwp0mcz0Rl-B_ijk");
            url.put("location", location.getLatitude() + "," + location.getLongitude());
            url.put("radius", 5000);

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            places = httpResponse.parseAs(PlacesList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Map.addPlaces(places);
    }
}
