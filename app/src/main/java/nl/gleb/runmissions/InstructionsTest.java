package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Gleb on 23/11/14.
 */
public class InstructionsTest extends Fragment {

    Comm comm;
    private static int condition;
    private Button startButton;
    private TextView counter;

    public static InstructionsIntro newInstance(Integer c) {
        condition = c;
        return new InstructionsIntro();
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

        counter = (TextView) getActivity().findViewById(R.id.hapticTestCounter);

        startButton = (Button) getActivity().findViewById(R.id.hapticButtonCloser);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                counter.setText("First trial starts in 20 seconds.");
                counter.setVisibility(View.VISIBLE);
                startHapticTest();
            }

            private void startHapticTest() {
            }
        });
    }
}