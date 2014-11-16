package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gleb on 03/11/14.
 */
public class Profile extends Fragment {

    Comm comm;
    AvatarAdapter adapter;

    TextView level, exp, missions, username;
    ViewPager viewPager;

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_profile));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        level = (TextView) getActivity().findViewById(R.id.level);
        exp = (TextView) getActivity().findViewById(R.id.exp);
        missions = (TextView) getActivity().findViewById(R.id.missions);
        username = (TextView) getActivity().findViewById(R.id.username);

        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
        adapter = new AvatarAdapter(getActivity().getApplicationContext());
        viewPager.setAdapter(adapter);

        setUserInfo(comm.getUser());
    }

    public void setUserInfo(User user) {
        if (user != null) {
            level.setText("Lvl " + user.getLevel());
            exp.setText("Exp " + user.getExp());
            missions.setText("Missions " + user.getMissions());
            username.setText(user.getUsername());
        }
    }
}
