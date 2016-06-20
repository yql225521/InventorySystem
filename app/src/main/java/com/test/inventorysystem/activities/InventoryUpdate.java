package com.test.inventorysystem.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.models.AssetModel;

public class InventoryUpdate extends AppCompatActivity {

    private Button btnConfirm;
    private Button btnCancel;
    private TextView textViewCode;
    private TextView textViewName;
    private RatingBar ratingBar;
    private ListView listView;
    private AssetModel asset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_update);
        initialization();
    }

    private void initialization() {
        btnConfirm = (Button) findViewById(R.id.button_asset_update_confirm);
        btnCancel = (Button) findViewById(R.id.button_asset_update_cancel);
        textViewCode = (TextView) findViewById(R.id.textView_asset_update_code);
        textViewName = (TextView) findViewById(R.id.textView_asset_update_name);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        listView = (ListView) findViewById(R.id.listView_asset_update);

        Bundle bundle = getIntent().getExtras();
        Object object = bundle.getSerializable("asset");
        asset = (AssetModel) object;
        textViewCode.setText(asset.getAssetCode());
        textViewName.setText(asset.getAssetName());
    }
}
