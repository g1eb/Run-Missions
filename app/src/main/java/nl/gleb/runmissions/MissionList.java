package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Gleb on 26/10/14.
 */
public class MissionList extends Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";

    ListView mission_list;
    String[] mission_titles, mission_descriptions;

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

        Resources res = getResources();
        mission_titles = res.getStringArray(R.array.mission_titles);
        mission_descriptions = res.getStringArray(R.array.mission_descriptions);

        mission_list = (ListView) getActivity().findViewById(R.id.missionList);
        mission_list.setAdapter(new MissionListAdapter(
                getActivity().getApplicationContext(),
                mission_titles,
                mission_descriptions
        ));
        mission_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Toast.makeText(getActivity().getApplicationContext(), "Mission selected: " + mission_titles[position], Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getActivity().getApplicationContext(), "Mission selected: " + mission_titles[position], Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getActivity().getApplicationContext(), "Mission selected: " + mission_titles[position], Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

class MissionListAdapter extends ArrayAdapter<String> {

    private String[] titles, descriptions;

    public MissionListAdapter(Context context, String[] titles, String[] descriptions) {
        super(context, R.layout.mission_list_item, R.id.missionListItemTitle, titles);
        this.titles = titles;
        this.descriptions = descriptions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;

        if ( item == null ) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.mission_list_item, parent, false);
        }

        TextView title = (TextView) item.findViewById(R.id.missionListItemTitle);
        title.setText(titles[position]);

        TextView desc = (TextView) item.findViewById(R.id.missionListItemDesc);
        desc.setText(descriptions[position]);

        return item;
    }
}