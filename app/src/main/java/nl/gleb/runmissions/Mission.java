package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Gleb on 27/10/14.
 */
public class Mission extends Fragment implements View.OnClickListener {

    private static final String ARG_SECTION_TITLE = "section_title";
    Comm comm;
    Button startBtn;
    TextView missionDesc;
    Chronometer timer;
    static int mission_id;
    static String mission_title;

    public static Mission newInstance(int id, String title) {
        mission_id = id;
        mission_title = title;

        Mission fragment = new Mission();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_TITLE, mission_title);
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

        Resources res = getResources();
        String[] mission_descriptions = res.getStringArray(R.array.mission_descriptions);

        missionDesc = (TextView) getActivity().findViewById(R.id.missionDescription);
        missionDesc.setText(mission_descriptions[mission_id]);

        startBtn = (Button) getActivity().findViewById(R.id.startMissionButton);
        startBtn.setOnClickListener(this);

        timer = (Chronometer) getActivity().findViewById(R.id.mission_timer);
        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                CharSequence text = chronometer.getText();
                if (text.length() == 5) {
                    chronometer.setText("00:" + text);
                } else if (text.length() == 7) {
                    chronometer.setText("0" + text);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startMissionButton:
                if (Main.places.size() > 0) {
                    Random generator = new Random();
                    Object[] values = Main.places.values().toArray();
                    Main.mission = mission_title;
                    Location current = ((Main) getActivity()).mCurrentLocation;
                    Location tLocation = new Location("targetLocation");
                    Place target;
                    do {
                        target = (Place) values[generator.nextInt(values.length)];
                        tLocation.setLatitude(target.geometry.location.lat);
                        tLocation.setLongitude(target.geometry.location.lng);
                    } while (current.distanceTo(tLocation) >= 1000);
                    if (target != null) {
                        comm.setTarget(target);
                        new DirectionsFetcher(((Main) getActivity()).mCurrentLocation, target).execute();
                    }
                    initializeMission();
                }
                break;
        }
    }

    private void initializeMission() {
        missionDesc.setVisibility(View.GONE);
        startBtn.setVisibility(View.GONE);

        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(comm.getPattern("sprint"), -1);

        timer.setBase(SystemClock.elapsedRealtime());
        timer.setText("00:00:00");
        timer.setVisibility(View.VISIBLE);
        timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }
}
