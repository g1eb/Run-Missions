package nl.gleb.runmissions;

import com.google.api.client.util.Key;

import java.util.List;

public class PlacesList {

    @Key("status")
    public String status;

    @Key("next_page_token")
    public String next_page_token;

    @Key("results")
    public List<Place> results;
}
