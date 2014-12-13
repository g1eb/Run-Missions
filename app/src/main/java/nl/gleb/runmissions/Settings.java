package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Gleb on 27/10/14.
 */
public class Settings extends Fragment implements SeekBar.OnSeekBarChangeListener {

    Comm comm;
    SeekBar distanceInput, sprintsInput, feedbackRateInput;
    TextView selectedDistance, selectedSprints, selectedFeedbackRate;

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

        Resources res = getResources();

        // Init seekbars
        selectedDistance = (TextView) getActivity().findViewById(R.id.selectedDistance);
        selectedDistance.setText(String.valueOf(Main.distance));

        distanceInput = (SeekBar) getActivity().findViewById(R.id.seekbarDistance);
        distanceInput.setOnSeekBarChangeListener(this);
        distanceInput.setMax(res.getInteger(R.integer.max_distance));
        distanceInput.setProgress(Main.distance);

        selectedSprints = (TextView) getActivity().findViewById(R.id.selectedSprints);
        selectedSprints.setText(String.valueOf(Main.sprints));

        sprintsInput = (SeekBar) getActivity().findViewById(R.id.seekbarSprints);
        sprintsInput.setOnSeekBarChangeListener(this);
        sprintsInput.setMax(res.getInteger(R.integer.max_sprints));
        sprintsInput.setProgress(Main.sprints);

        selectedFeedbackRate = (TextView) getActivity().findViewById(R.id.selectedFeedbackRate);
        selectedFeedbackRate.setText(String.valueOf(Main.feedbackRate * 10));

        feedbackRateInput = (SeekBar) getActivity().findViewById(R.id.seekbarFeedbackRate);
        feedbackRateInput.setOnSeekBarChangeListener(this);
        feedbackRateInput.setMax(res.getInteger(R.integer.max_feedback_rate));
        feedbackRateInput.setProgress(Main.feedbackRate);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) return;

        SharedPreferences prefs = getActivity().getSharedPreferences(Main.SETTINGS_TAG, Context.MODE_PRIVATE);

        switch (seekBar.getId()) {
            case R.id.seekbarDistance:
                prefs.edit().putInt("distance", progress).apply();
                selectedDistance.setText(String.valueOf(progress));
                Main.distance = progress;
                Main.places.clear();
                break;
            case R.id.seekbarSprints:
                prefs.edit().putInt("sprints", progress).apply();
                selectedSprints.setText(String.valueOf(progress));
                Main.sprints = progress;
                break;
            case R.id.seekbarFeedbackRate:
                if (progress == 0) {
                    progress += 1;
                    seekBar.setProgress(progress);
                }
                prefs.edit().putInt("feedbackRate", progress).apply();
                selectedFeedbackRate.setText(String.valueOf(progress * 10));
                Main.feedbackRate = progress;
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
