package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Gleb on 23/11/14.
 */
public class InstructionsTest extends Fragment {

    private static final int HAPTIC_TEST_INTERVAL = 10000; //millis
    private static final int NUM_TRIALS_PER_SET = 15;
    private static final String INSTRUCTIONS_TAG = "INSTRUCTIONS";

    Comm comm;
    private static int condition;
    private int counter = 0;
    Handler handler;
    Runnable runnable;
    private ArrayList<Integer> trials = new ArrayList<Integer>();
    private ArrayList<Integer> allTrials = new ArrayList<Integer>();
    private Button startButton;
    private TextView counterText;

    public static InstructionsTest newInstance(Integer c) {
        condition = c;
        return new InstructionsTest();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_instructions));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.instructions_test, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        counterText = (TextView) getActivity().findViewById(R.id.hapticTestCounter);

        startButton = (Button) getActivity().findViewById(R.id.startHapticTest);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                counterText.setText("First trial starts in 10 seconds.");
                counterText.setVisibility(View.VISIBLE);
                startHapticTest();
            }

            private void startHapticTest() {
                Log.e("I", "start haptic test called, condition: " + condition);
                allTrials = getTrialsFromPrefs();
                Log.e("I", "all trials from prefs: " + allTrials.toString());
                Integer numTrials = getNumTrials(condition);
                Log.e("I", "num trials from prefs: " + numTrials);

                if (numTrials % NUM_TRIALS_PER_SET != 0) {
                    Log.e("I", "somebody did not finish a test set, update numTrials");
                    numTrials = numTrials - (numTrials % NUM_TRIALS_PER_SET);

                    String tag = "num_trials_con_" + condition;
                    SharedPreferences prefs = getActivity().getSharedPreferences(INSTRUCTIONS_TAG, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(tag, numTrials);
                    editor.commit();
                }

                if (allTrials.size() > numTrials) {
                    Log.e("I", "going to run existing set: " + trials.toString());
                    trials = new ArrayList(allTrials.subList(numTrials, numTrials + NUM_TRIALS_PER_SET));
                } else if (allTrials.size() == numTrials) {
                    Log.e("I", "make new trials add them to the prefs");

                    trials = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5));
                    Collections.shuffle(trials);

                    updateTrialsInPrefs(trials);
                }

                Log.e("I", "set selected: " + trials.toString());
                comm.setSelectedHapticTest(trials.toString());

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (counter >= NUM_TRIALS_PER_SET) {
                            Log.e("I", "removing callbacks");
                            handler.removeCallbacks(runnable);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.container, InstructionsThanks.newInstance()).commit();
                            return;
                        }

                        counterText.setText("Trial " + (counter + 1));

                        int trial = trials.get(counter);
                        Log.e("I", "current trial is: " + trial);

                        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        switch (trial) {
                            case 1:
                                vibrator.vibrate(comm.getPattern("left" + condition), -1);
                                break;
                            case 2:
                                vibrator.vibrate(comm.getPattern("right" + condition), -1);
                                break;
                            case 3:
                                vibrator.vibrate(comm.getPattern("accelerate" + condition), -1);
                                break;
                            case 4:
                                vibrator.vibrate(comm.getPattern("error" + condition), -1);
                                break;
                            case 5:
                                vibrator.vibrate(comm.getPattern("closer" + condition), -1);
                                break;
                        }

                        updateNumTrials(condition);

                        counter += 1;
                        handler.postDelayed(this, HAPTIC_TEST_INTERVAL);
                    }
                };
                handler.postDelayed(runnable, HAPTIC_TEST_INTERVAL);
            }
        });
    }

    private Integer getNumTrials(int condition) {
        String tag = "num_trials_con_" + condition;
        SharedPreferences prefs = getActivity().getSharedPreferences(INSTRUCTIONS_TAG, Context.MODE_PRIVATE);
        return prefs.getInt(tag, 0);
    }

    private void updateNumTrials(int condition) {
        String tag = "num_trials_con_" + condition;
        SharedPreferences prefs = getActivity().getSharedPreferences(INSTRUCTIONS_TAG, Context.MODE_PRIVATE);
        int counter = prefs.getInt(tag, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(tag, ++counter);
        editor.commit();
    }

    private ArrayList<Integer> getTrialsFromPrefs() {
        ArrayList<Integer> trials = new ArrayList<Integer>();
        SharedPreferences prefs = getActivity().getSharedPreferences(INSTRUCTIONS_TAG, Context.MODE_PRIVATE);
        String allTrialsPrefs = prefs.getString("trial_sets", "");
        if (!allTrialsPrefs.equals("")) {
            for (String trial : allTrialsPrefs.split(",")) {
                trials.add(Integer.parseInt(trial));
            }
        }
        return trials;
    }

    private void updateTrialsInPrefs(ArrayList<Integer> trials) {
        allTrials.addAll(trials);
        String allTrialsPrefs = "";
        for (int trial : allTrials) {
            allTrialsPrefs += "" + trial + ",";
        }
        SharedPreferences prefs = getActivity().getSharedPreferences(INSTRUCTIONS_TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("trial_sets", allTrialsPrefs);
        editor.commit();
    }
}