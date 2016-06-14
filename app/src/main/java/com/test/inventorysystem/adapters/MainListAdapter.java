package com.test.inventorysystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.inventorysystem.R;

/**
 * Created by youmengli on 6/3/16.
 */

public class MainListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mainInflater;
    private String[] titles;
    private int[] icons;

    public MainListAdapter(Context c, String[] titles, int[] icons) {
        mContext = c;
        this.titles = titles;
        this.icons = icons;
    }

    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        this.mainInflater = LayoutInflater.from(this.mContext);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.mainInflater.inflate(R.layout.adapter_main_grid_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.main_grid_item_icon);
            holder.title = (TextView) convertView.findViewById(R.id.main_grid_item_title);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item_title = this.titles[position];
        int item_icon = this.icons[position];

        holder.title.setText(item_title);
        holder.icon.setImageResource(item_icon);

        return convertView;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView title;
    }
}