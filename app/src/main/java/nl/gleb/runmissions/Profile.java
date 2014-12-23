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
public class Profile extends Fragment implements ViewPager.OnPageChangeListener {

    Comm comm;
    AvatarAdapter adapter;

    TextView level, exp, missions, target, username;
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
        target = (TextView) getActivity().findViewById(R.id.target);
        username = (TextView) getActivity().findViewById(R.id.username);

        adapter = new AvatarAdapter(getActivity().getApplicationContext());
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);
        viewPager.setOnPageChangeListener(this);
        viewPager.setAdapter(adapter);

        setUserInfo(comm.getUser());
    }

    public void setUserInfo(User user) {
        if (user != null) {
            level.setText("Lvl " + user.getLevel());
            exp.setText("Exp " + user.getExp());
            missions.setText("Missions " + user.getMissions());
            username.setText(user.getUsername());
            target.setText(user.getTarget().split("\\sL\\(")[0]);

            int resID = getResources().getIdentifier(user.getAvatar(), "drawable", getActivity().getPackageName());
            viewPager.setCurrentItem(adapter.getAvatarPosition(resID), true);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        comm.updateUserAvatar(getResources().getResourceEntryName(adapter.getItem(i)));
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
