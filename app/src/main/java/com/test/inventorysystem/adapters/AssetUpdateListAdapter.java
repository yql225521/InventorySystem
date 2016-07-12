package com.test.inventorysystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.inventorysystem.R;

import java.util.ArrayList;

/**
 * Created by youmengli on 6/20/16.
 */

public class AssetUpdateListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<String> titleList;
    private ArrayList<String> infoList;
    private ArrayList<String> checkedList = new ArrayList<>();

    public AssetUpdateListAdapter(Context ctx, ArrayList<String> titles, ArrayList<String> infos) {
        mContext = ctx;
        this.titleList = titles;
        this.infoList = infos;
    }

    @Override
    public int getCount() {
        return this.titleList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.titleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.layoutInflater = LayoutInflater.from(this.mContext);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.layoutInflater.inflate(R.layout.adapter_asset_update_list_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.asset_update_list_title);
            holder.info = (TextView) convertView.findViewById(R.id.asset_update_list_info);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox_asset_update_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkedList.add(getCheckedItem(position));
                } else {
                    checkedList.remove(getCheckedItem(position));
                }
            }
        });

        holder.title.setText(this.titleList.get(position));
        holder.info.setText(this.infoList.get(position));

        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public TextView info;
        public CheckBox checkBox;
    }

    private String getCheckedItem(int position) {
        switch (position) {
            case 0:
                return "organName";
            case 1:
                return "operator";
            case 2:
                return "storage";
            case 3:
                return "status";
        }
        return null;
    }

    public ArrayList<String> getCheckedList() {
        return this.checkedList;
    }
}
