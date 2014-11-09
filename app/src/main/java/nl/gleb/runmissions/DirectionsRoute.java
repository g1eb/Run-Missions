package nl.gleb.runmissions;

import com.google.api.client.util.Key;

import java.util.List;

public class DirectionsRoute {
    @Key("overview_polyline")
    public OverviewPolyLine overviewPolyLine;

    @Key("bounds")
    public DirectionsBounds bounds;

    @Key("Legs")
    public List<DirectionsLegs> legs;
}