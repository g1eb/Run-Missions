package nl.gleb.runmissions;

import com.google.api.client.util.Key;

public class DirectionsStep {
    @Key("distance")
    public DirectionsDistance distance;

    @Key("duration")
    public DirectionsDuration duration;

    @Key("start_location")
    public PlaceLocation start_location;

    @Key("end_location")
    public PlaceLocation end_location;

    @Key("html_instructions")
    public String html_instructions;
}