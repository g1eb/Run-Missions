package nl.gleb.runmissions;

import com.google.api.client.util.Key;

import java.util.List;

public class DirectionsLegs {
    @Key("steps")
    public List<DirectionsStep> steps;
}