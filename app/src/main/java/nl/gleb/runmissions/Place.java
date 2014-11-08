package nl.gleb.runmissions;

import com.google.api.client.util.Key;

public class Place {

    @Key("id")
    public String id;

    @Key("name")
    public String name;

    @Key("types")
    public String[] types;

    @Key("icon")
    public String icon;

    @Key("vicinity")
    public String vicinity;
}
