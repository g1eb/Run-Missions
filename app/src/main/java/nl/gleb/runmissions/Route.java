package nl.gleb.runmissions;

import com.google.api.client.util.Key;

public class Route {
    @Key("overview_polyline")
    public OverviewPolyLine overviewPolyLine;

    @Key("bounds")
    public DirectionsBounds bounds;
}