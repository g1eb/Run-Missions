package nl.gleb.runmissions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Gleb on 26/10/14.
 */
public class MissionList extends Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    ListView missions_list;
    String[] missions;

    public static MissionList newInstance(int sectionNumber) {
        MissionList fragment = new MissionList();
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
        return inflater.inflate(R.layout.mission_list, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        missions = new String[]{
                getString(R.string.title_mission1),
                getString(R.string.title_mission2),
                getString(R.string.title_mission3)
        };

        missions_list = (ListView) getActivity().findViewById(R.id.missionList);
        missions_list.setAdapter(new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.mission_list_item,
                R.id.missionListItem,
                missions
        ));
        missions_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Toast.makeText(getActivity().getApplicationContext(), "Mission selected: "+missions[position], Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getActivity().getApplicationContext(), "Mission selected: "+missions[position], Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity().getApplicationContext(), "Mission selected: "+missions[position], Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
