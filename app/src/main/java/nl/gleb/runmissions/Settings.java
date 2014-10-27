package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by Gleb on 27/10/14.
 */
public class Settings extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    SeekBar distanceInput, eventsInput;

    public static Settings newInstance(int sectionNumber) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((Main) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        distanceInput = (SeekBar) getActivity().findViewById(R.id.distanceInput);
        eventsInput = (SeekBar) getActivity().findViewById(R.id.eventsInput);

        distanceInput.setOnSeekBarChangeListener(this);
        eventsInput.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        Toast.makeText(getActivity().getApplicationContext(), "Progress: " + Integer.toString(progress), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
