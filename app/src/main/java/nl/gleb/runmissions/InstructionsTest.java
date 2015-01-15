package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Gleb on 23/11/14.
 */
public class InstructionsTest extends Fragment {

    private static final int HAPTIC_TEST_INTERVAL = 10000; //millis
    private static final int NUM_TRIALS_PER_SET = 15;

    Comm comm;
    private static int condition;
    private int counter = 0;
    Handler handler;
    Runnable runnable;
    private Button startButton, backButton;
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
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (counter >= NUM_TRIALS_PER_SET) {
                            handler.removeCallbacks(runnable);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                            ft.replace(R.id.container, InstructionsThanks.newInstance()).commit();
                            return;
                        }

                        counter += 1;
                        handler.postDelayed(this, HAPTIC_TEST_INTERVAL);
                    }
                };
                handler.postDelayed(runnable, HAPTIC_TEST_INTERVAL);
            }
        });

        backButton = (Button) getActivity().findViewById(R.id.hapticTestBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.container, Instructions.newInstance()).commit();
            }
        });
    }
}