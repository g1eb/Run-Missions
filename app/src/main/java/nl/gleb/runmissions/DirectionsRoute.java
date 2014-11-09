package nl.gleb.runmissions;

import com.google.api.client.util.Key;

import java.util.List;

public class DirectionsRoute {
    @Key("bounds")
    public DirectionsBounds bounds;

    @Key("legs")
    public List<DirectionsLeg> legs;

    @Key("overview_polyline")
    public OverviewPolyLine overviewPolyLine;
}