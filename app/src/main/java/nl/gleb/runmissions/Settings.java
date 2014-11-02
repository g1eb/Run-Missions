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

    Comm comm;
    SeekBar distanceInput, eventsInput;

    public static Settings newInstance() {
        return new Settings();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_settings));
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
