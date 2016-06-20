package com.test.inventorysystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
    private ArrayList<String> msgList;

    public AssetUpdateListAdapter(Context ctx, ArrayList<String> title, ArrayList<String> msg) {
        this.mContext = ctx;
        this.titleList = title;
        this.msgList = msg;
    }

    @Override
    public int getCount() {
        return 0;
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
            view = this.layoutInflater.inflate(R.layout.adapter_asset_update_list_item, null);
            holder.title = (TextView) view.findViewById(R.id.textView_asset_update_list_title);
            holder.msg = (TextView) view.findViewById(R.id.textView_asset_update_list_msg);
            holder.checkBox = (CheckBox) view.findViewById(R.id.checkBox_asset_update_list);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText(this.titleList.get(i));
        holder.msg.setText(this.msgList.get(i));
        return null;
    }

    class ViewHolder {
        public TextView title;
        public TextView msg;
        public CheckBox checkBox;
    }
}
