package nl.gleb.runmissions;

import com.google.api.client.util.Key;

import java.util.List;

public class DirectionsResult {
    @Key("routes")
    public List<Route> routes;
}