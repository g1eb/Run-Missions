package nl.gleb.runmissions;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by Gleb on 08/11/14.
 */
public class DirectionsFetcher extends AsyncTask<URL, Integer, String> {

    Place target;

    public DirectionsFetcher(Place target) {
        this.target = target;
    }

    @Override
    protected String doInBackground(URL... params) {
        return null;
    }

    public void startNavigation() {
    }
}
