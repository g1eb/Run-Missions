package nl.gleb.runmissions;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Gleb on 25/10/14.
 */
public class Instructions extends Fragment {

    static final long[] START_PATTERN = {0, 1000};
    static final long[] LEFT_PATTERN = {0, 300, 150, 100};
    static final long[] RIGHT_PATTERN = {0, 100, 150, 300};
    static final long[] ACCELERATE_PATTERN = {0, 100, 100, 100, 100, 100};
    static final long[] ERROR_PATTERN = {0, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300};
    static final long[] FINISH_PATTERN = {0, 1000};

    private Button startBtn, leftBtn, rightBtn, accelerateBtn, errorBtn, finishBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.instructions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
         * Haptic signal indicating to start running
         */
        startBtn = (Button) getActivity().findViewById(R.id.hapticButtonStart);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(START_PATTERN, -1);
            }
        });

        /*
         * Haptic signal indicating to go left
         */
        leftBtn = (Button) getActivity().findViewById(R.id.hapticButtonLeft);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(LEFT_PATTERN, -1);
            }
        });
        /*
         * Haptic signal indicating to go right
         */
        rightBtn = (Button) getActivity().findViewById(R.id.hapticButtonRight);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(RIGHT_PATTERN, -1);
            }
        });

        /*
         * Haptic signal indicating to accelerate
         */
        accelerateBtn = (Button) getActivity().findViewById(R.id.hapticButtonAccelerate);
        accelerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(ACCELERATE_PATTERN, -1);
            }
        });

        /*
         * Haptic signal indicating that there is an error (wrong direction)
         */
        errorBtn = (Button) getActivity().findViewById(R.id.hapticButtonError);
        errorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(ERROR_PATTERN, -1);
            }
        });

        /*
         * Haptic signal indicating that user has reached his goal
         */
        finishBtn = (Button) getActivity().findViewById(R.id.hapticButtonFinish);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(FINISH_PATTERN, -1);
            }
        });
    }
}
