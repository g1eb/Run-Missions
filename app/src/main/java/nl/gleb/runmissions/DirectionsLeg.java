package nl.gleb.runmissions;

import com.google.api.client.util.Key;

import java.util.List;

public class DirectionsLeg {
    @Key("distance")
    public DirectionsDistance distance;

    @Key("duration")
    public DirectionsDuration duration;

    @Key("steps")
    public List<DirectionsStep> steps;
}