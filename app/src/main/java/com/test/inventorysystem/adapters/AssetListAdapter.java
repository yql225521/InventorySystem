package com.test.inventorysystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.models.AssetModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youmengli on 6/12/16.
 */

public class AssetListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater assetListInflater;
    private ArrayList<AssetModel> assetList;

    public AssetListAdapter (Context ctx, ArrayList<AssetModel> assetList) {
        this.mContext = ctx;
        this.assetList = new ArrayList<AssetModel>(assetList);
    }

    @Override
    public int getCount() {
        return assetList.size();
    }

    @Override
    public Object getItem(int i) {
        return assetList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addAll(List<AssetModel> items){
        this.assetList.addAll(items);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.assetListInflater = LayoutInflater.from(this.mContext);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.assetListInflater.inflate(R.layout.adapter_asset_list_item, null);
            holder.serial_num = (TextView) convertView.findViewById(R.id.textView_serial_number);
            holder.asset_code = (TextView) convertView.findViewById(R.id.textView_asset_code);
            holder.asset_name = (TextView) convertView.findViewById(R.id.textView_asset_name);
            holder.asset_type = (TextView) convertView.findViewById(R.id.textView_asset_type);
            holder.asset_place = (TextView) convertView.findViewById(R.id.textView_asset_place);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        String serialNum = String.valueOf(position + 1);
        String assetCode = this.assetList.get(position).getAssetCode();
        String assetName = this.assetList.get(position).getAssetName();
        String assetType = this.assetList.get(position).getCateName();
        String assetPlace = this.assetList.get(position).getStorageDescr();
        holder.serial_num.setText(serialNum);
        holder.asset_code.setText(assetCode);
        holder.asset_name.setText(assetName);
        holder.asset_type.setText(assetType);
        holder.asset_place.setText(assetPlace);
        return convertView;
    }

    class ViewHolder {
        public TextView serial_num;
        public TextView asset_code;
        public TextView asset_name;
        public TextView asset_type;
        public TextView asset_place;
    }
}
