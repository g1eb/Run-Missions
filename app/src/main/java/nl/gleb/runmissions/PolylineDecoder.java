package nl.gleb.runmissions;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gleb on 08/11/14.
 */

public class PolylineDecoder {

    public static List<LatLng> decodePoints(String encoded_points){
        int index = 0;
        int lat = 0;
        int lng = 0;
        List <LatLng> out = new ArrayList<LatLng>();

        try {
            int shift;
            int result;
            while (index < encoded_points.length()) {
                shift = 0;
                result = 0;
                while (true) {
                    int b = encoded_points.charAt(index++) - '?';
                    result |= ((b & 31) << shift);
                    shift += 5;
                    if (b < 32)
                        break;
                }
                lat += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);

                shift = 0;
                result = 0;
                while (true) {
                    int b = encoded_points.charAt(index++) - '?';
                    result |= ((b & 31) << shift);
                    shift += 5;
                    if (b < 32)
                        break;
                }
                lng += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
                /* Add the new Lat/Lng to the Array. */
                out.add(new LatLng((lat*10),(lng*10)));
            }
            return out;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}
