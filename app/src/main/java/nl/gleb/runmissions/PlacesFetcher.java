package nl.gleb.runmissions;

import android.location.Location;
import android.os.AsyncTask;

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

/**
 * Created by Gleb on 10/11/14.
 */
public class PlacesFetcher extends AsyncTask<URL, Integer, String> {

    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();

    HttpRequestFactory requestFactory;

    Location location;
    String next_page_token;
    PlacesList places;
    Main main;

    GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json");

    public PlacesFetcher(Main activity) {
        this.main = activity;
    }

    public PlacesFetcher(Main activity, GenericUrl url, String next_page_token) {
        this.main = activity;
        this.url = url;
        this.next_page_token = next_page_token;
    }

    @Override
    protected String doInBackground(URL... params) {
        if (location == null) {
            location = main.mCurrentLocation;
        }

        try {
            requestFactory = HTTP_TRANSPORT.createRequestFactory(
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(JSON_FACTORY));
                        }
                    }
            );

            url.put("key", "AIzaSyCbFFLGTKvJh_on6sRgwp0mcz0Rl-B_ijk");
            url.put("location", location.getLatitude() + "," + location.getLongitude());
            url.put("radius", 2500);

            if (next_page_token != null) {
                url.put("pagetoken", next_page_token);
            }

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
        main.updatePlaces(places, url);
    }
}
