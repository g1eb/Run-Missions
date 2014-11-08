package nl.gleb.runmissions;

import com.google.api.client.util.Key;

public class Place {
    @Key("name")
    public String name;

    @Key("id")
    public String id;

    @Key("types")
    public String[] types;

    @Key("vicinity")
    public String vicinity;
}
