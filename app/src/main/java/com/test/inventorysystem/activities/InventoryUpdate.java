package com.test.inventorysystem.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetUpdateListAdapter;
import com.test.inventorysystem.models.AssetModel;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

public class InventoryUpdate extends AppCompatActivity {

    public final static int RESULT_CODE=4;
    private RatingBar ratingBar;
    private EditText editTextNote;
    private Bundle bundle;

    String[] titles = {"使用部门", "使用人", "存放地点", "资产状态"};

    private ArrayList<String> updateTitles;

    private ListView listView;
    AssetUpdateListAdapter assetUpdateListAdapter;
    AssetModel asset;

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
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        editTextNote = (EditText) findViewById(R.id.editText_inv_note);

        bundle = getIntent().getExtras();
        Object object = bundle.getSerializable("asset");
        asset = (AssetModel) object;
        textViewCode.setText(asset.getAssetCode());
        textViewName.setText(asset.getAssetName());

        updateTitles = new ArrayList<>();
        updateTitles.add("使用部门");
        updateTitles.add("使用人");
        updateTitles.add("存放地点");
        updateTitles.add("资产状态");

        ArrayList<String> updateInfo = new ArrayList<>();
        updateInfo.add(asset.getOrganName());
        updateInfo.add(asset.getOperator());
        updateInfo.add(asset.getStorage());
        updateInfo.add(asset.getStatus());

        listView = (ListView) findViewById(R.id.listView_asset_update_list);
        assetUpdateListAdapter = new AssetUpdateListAdapter(this, updateTitles, updateInfo);
        if (listView != null) {
            listView.setAdapter(assetUpdateListAdapter);
        } else {
            System.out.println("no list");
        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("confirm");
                String checkedItems = "";
                Intent intent = new Intent();
                Bundle bundleBack = new Bundle();
                ArrayList<String> checkList = assetUpdateListAdapter.getCheckedList();
                for (String checkItem : checkList) {
                    checkedItems += "," + checkItem;
                }
                if(StringUtils.isNotBlank(checkedItems)){
                    checkedItems = checkedItems.substring(1);
                }
                asset.setInvNote(editTextNote.getText().toString().trim());
                asset.setDisCodes(checkedItems);
                asset.setStarNum(Double.parseDouble(String.valueOf(ratingBar.getRating())));
                bundleBack.putSerializable("asset", asset);
                intent.putExtras(bundleBack);
                setResult(RESULT_CODE, intent);
                InventoryUpdate.this.finish();
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
