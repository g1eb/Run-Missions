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
 * Created by Gleb on 08/11/14.
 */
public class DirectionsFetcher extends AsyncTask<URL, Integer, String> {

    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();

    HttpRequestFactory requestFactory;

    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private DirectionsBounds bounds;

    Location origin;
    Place target;

    public DirectionsFetcher(Location origin, Place target) {
        this.origin = origin;
        this.target = target;
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

            GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
            url.put("origin", origin.getLatitude() + "," + origin.getLongitude());
            url.put("destination", target.geometry.location.lat + "," + target.geometry.location.lng);
            url.put("mode", "walking");

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();

            DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
            bounds = directionsResult.routes.get(0).bounds;

            String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
            latLngs = PolylineDecoder.decodePoints(encodedPoints);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Map.addPolyLine(latLngs);
        Map.setBounds(bounds);
    }
}