package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Gleb on 23/11/14.
 */
public class Instructions extends Fragment implements View.OnClickListener {

    Comm comm;
    private Button leftBtn, rightBtn, sprintBtn, errorBtn, closerBtn;

    public static Instructions newInstance() {
        return new Instructions();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_instructions));
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

        sprintBtn = (Button) getActivity().findViewById(R.id.hapticButtonSprint);
        sprintBtn.setOnClickListener(this);

        errorBtn = (Button) getActivity().findViewById(R.id.hapticButtonError);
        errorBtn.setOnClickListener(this);

        closerBtn = (Button) getActivity().findViewById(R.id.hapticButtonCloser);
        closerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        switch (v.getId()) {
            case R.id.hapticButtonLeft:
                vibrator.vibrate(comm.getPattern("left"), -1);
                break;
            case R.id.hapticButtonRight:
                vibrator.vibrate(comm.getPattern("right"), -1);
                break;
            case R.id.hapticButtonSprint:
                vibrator.vibrate(comm.getPattern("sprint"), -1);
                break;
            case R.id.hapticButtonError:
                vibrator.vibrate(comm.getPattern("error"), -1);
                break;
            case R.id.hapticButtonCloser:
                vibrator.vibrate(comm.getPattern("closer"), -1);
                break;
        }
    }
}