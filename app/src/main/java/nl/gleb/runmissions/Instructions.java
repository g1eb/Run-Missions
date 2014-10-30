package nl.gleb.runmissions;

import android.app.Activity;
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
public class Instructions extends Fragment implements View.OnClickListener {

    static final long[] LEFT_PATTERN = {0, 300, 150, 100};
    static final long[] RIGHT_PATTERN = {0, 100, 150, 300};
    static final long[] ACCELERATE_PATTERN = {0, 100, 100, 100, 100, 100};
    static final long[] ERROR_PATTERN = {0, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300, 300, 150, 200, 300};
    static final long[] CLOSER_PATTERN = {0, 100, 1000, 100, 900, 150, 800, 150, 700, 200, 600, 200, 500, 250, 400, 250, 300, 300, 200, 300, 100, 3000};

    private static final String ARG_SECTION_NUMBER = "section_number";

    private Button leftBtn, rightBtn, accelerateBtn, errorBtn, closerBtn;

    public static Instructions newInstance(int sectionNumber) {
        Instructions fragment = new Instructions();
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
        return inflater.inflate(R.layout.instructions, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        leftBtn = (Button) getActivity().findViewById(R.id.hapticButtonLeft);
        leftBtn.setOnClickListener(this);

        rightBtn = (Button) getActivity().findViewById(R.id.hapticButtonRight);
        rightBtn.setOnClickListener(this);

        accelerateBtn = (Button) getActivity().findViewById(R.id.hapticButtonAccelerate);
        accelerateBtn.setOnClickListener(this);

        errorBtn = (Button) getActivity().findViewById(R.id.hapticButtonError);
        errorBtn.setOnClickListener(this);

        closerBtn = (Button) getActivity().findViewById(R.id.hapticButtonCloser);
        closerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        switch (v.getId()) {
            case R.id.hapticButtonLeft:
                vibrator.vibrate(LEFT_PATTERN, -1);
                break;
            case R.id.hapticButtonRight:
                vibrator.vibrate(RIGHT_PATTERN, -1);
                break;
            case R.id.hapticButtonAccelerate:
                vibrator.vibrate(ACCELERATE_PATTERN, -1);
                break;
            case R.id.hapticButtonError:
                vibrator.vibrate(ERROR_PATTERN, -1);
                break;
            case R.id.hapticButtonCloser:
                vibrator.vibrate(CLOSER_PATTERN, -1);
                break;
        }
    }
}
