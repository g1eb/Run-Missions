package nl.gleb.runmissions;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Gleb on 26/10/14.
 */
public class MissionList extends Fragment implements AdapterView.OnItemClickListener {

    Comm comm;
    ListView mission_list;
    String[] mission_titles, mission_descriptions;

    public static MissionList newInstance() {
        return new MissionList();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_missions));
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
        TextView title = (TextView) view.findViewById(R.id.missionListItemTitle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, Mission.newInstance((String) title.getText())).commit();
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
        MissionListViewHolder holder = null;

        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.mission_list_item, parent, false);
            holder = new MissionListViewHolder(item);
            item.setTag(holder);
        } else {
            holder = (MissionListViewHolder) item.getTag();
        }

        holder.title.setText(titles[position]);
        holder.desc.setText(descriptions[position]);

        return item;
    }

    class MissionListViewHolder {
        TextView title, desc;

        MissionListViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.missionListItemTitle);
            desc = (TextView) view.findViewById(R.id.missionListItemDesc);
        }
    }
}