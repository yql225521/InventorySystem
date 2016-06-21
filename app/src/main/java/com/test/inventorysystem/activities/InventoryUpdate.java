package com.test.inventorysystem.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetUpdateListAdapter;
import com.test.inventorysystem.adapters.MainListAdapter;
import com.test.inventorysystem.adapters.TestAdapter;
import com.test.inventorysystem.models.AssetModel;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class InventoryUpdate extends AppCompatActivity {

    String[] titles = {"使用部门", "使用人", "存放地点", "资产状态"};
    String[] infos = {"1","2","3","4"};

    String[] mainGridTitles = {"资产查询", "资产盘点", "盘点情况", "资产扫描", "拍照上传", "离线盘点管理", "帮助"};
    int[] mainGridIcons = {R.drawable.ic_search_black_24dp, R.drawable.ic_local_atm_black_24dp,
            R.drawable.ic_content_paste_black_24dp, R.drawable.ic_crop_free_black_24dp,
            R.drawable.ic_camera_alt_black_24dp, R.drawable.ic_format_list_numbered_black_24dp,
            R.drawable.ic_info_black_24dp};

    private ListView listView;
    AssetUpdateListAdapter assetUpdateListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_update);
        initialization();
    }

    private void initialization() {
        Button btnConfirm = (Button) findViewById(R.id.button_asset_update_confirm);
        Button btnCancel = (Button) findViewById(R.id.button_asset_update_cancel);
        TextView textViewCode = (TextView) findViewById(R.id.textView_asset_update_code);
        TextView textViewName = (TextView) findViewById(R.id.textView_asset_update_name);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        Bundle bundle = getIntent().getExtras();
        Object object = bundle.getSerializable("asset");
        AssetModel asset = (AssetModel) object;
        textViewCode.setText(asset.getAssetCode());
        textViewName.setText(asset.getAssetName());

        ArrayList<String> updateInfo = new ArrayList<>();
        updateInfo.add(asset.getOrganName());
        updateInfo.add(asset.getOperator());
        updateInfo.add(asset.getStorage());
        updateInfo.add(asset.getStatus());

        listView = (ListView) findViewById(R.id.listView_asset_update_list);
        assetUpdateListAdapter = new AssetUpdateListAdapter(this, this.titles, updateInfo);
        if (listView != null) {
            listView.setAdapter(assetUpdateListAdapter);
        } else {
            System.out.println("no list");
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkedItems = "";
                ArrayList<String> checkList = assetUpdateListAdapter.getCheckedList();
                for (String checkItem : checkList) {
                    checkedItems += "," + checkItem;
                }
                if(StringUtils.isNotBlank(checkedItems)){
                    checkedItems = checkedItems.substring(1);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
