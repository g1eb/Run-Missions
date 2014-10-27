package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Gleb on 27/10/14.
 */
public class Mission extends Fragment {

    private static final String ARG_SECTION_TITLE = "section_title";
    Comm comm;
    static String mission_title;

    public static Mission newInstance(String mission) {
        mission_title = mission;
        Mission fragment = new Mission();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, mission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(mission_title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mission, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
