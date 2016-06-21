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
 * Created by youmengli on 6/21/16.
 */

public class TestAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater layoutInflater;
    private String[] titles;
    private int[] icons;

    public TestAdapter(Context c, String[] titles, int[] icons) {
        mContext = c;
        this.titles = titles;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return this.titles.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        this.layoutInflater = LayoutInflater.from(this.mContext);
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = this.layoutInflater.inflate(R.layout.adapter_main_grid_item, null);
            holder.title = (TextView) view.findViewById(R.id.main_grid_item_title);
            holder.icon = (ImageView) view.findViewById(R.id.main_grid_item_icon);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        String item_title = this.titles[i];
        int item_icon = this.icons[i];

        holder.title.setText(item_title);
        holder.icon.setImageResource(item_icon);

        return view;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView title;
    }
}
