package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gleb on 03/11/14.
 */
public class Profile extends Fragment {

    Comm comm;
    TextView level, exp, missions, username;

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_profile));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        User user = comm.getUser();

        level = (TextView) getActivity().findViewById(R.id.level);
        exp = (TextView) getActivity().findViewById(R.id.exp);
        missions = (TextView) getActivity().findViewById(R.id.missions);
        username = (TextView) getActivity().findViewById(R.id.username);
    }
}
