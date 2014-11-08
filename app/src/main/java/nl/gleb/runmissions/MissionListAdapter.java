package nl.gleb.runmissions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MissionListAdapter extends ArrayAdapter<String> {

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
