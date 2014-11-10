package nl.gleb.runmissions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Created by Gleb on 10/11/14.
 */
public class NavDrawerItemListAdapter extends ArrayAdapter<Integer> {

    private Integer[] icons;

    public NavDrawerItemListAdapter(Context context, Integer[] icons) {
        super(context, R.layout.nav_drawer_list_item, R.id.nav_drawer_item_icon, icons);
        this.icons = icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        NavDrawerItem holder = null;

        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);
            holder = new NavDrawerItem(item);
            item.setTag(holder);
        } else {
            holder = (NavDrawerItem) item.getTag();
        }

        holder.icon.setImageResource(icons[position]);

        return item;
    }

    class NavDrawerItem {
        ImageView icon;

        NavDrawerItem(View view) {
            icon = (ImageView) view.findViewById(R.id.nav_drawer_item_icon);
        }
    }
}