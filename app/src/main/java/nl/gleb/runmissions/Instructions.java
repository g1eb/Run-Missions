package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
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
    private Button hapticIntro1, hapticIntro2, hapticTest1, hapticTest2;

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

        hapticIntro1 = (Button) getActivity().findViewById(R.id.hapticIntro1);
        hapticIntro1.setOnClickListener(this);

        hapticIntro2 = (Button) getActivity().findViewById(R.id.hapticIntro2);
        hapticIntro2.setOnClickListener(this);

        hapticTest1 = (Button) getActivity().findViewById(R.id.hapticTest1);
        hapticTest1.setOnClickListener(this);

        hapticTest2 = (Button) getActivity().findViewById(R.id.hapticTest2);
        hapticTest2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.hapticIntro1:
                ft.replace(R.id.container, InstructionsIntro.newInstance(1)).commit();
                break;
            case R.id.hapticIntro2:
                ft.replace(R.id.container, InstructionsIntro.newInstance(2)).commit();
                break;
            case R.id.hapticTest1:
                break;
            case R.id.hapticTest2:
                break;
        }
    }
}