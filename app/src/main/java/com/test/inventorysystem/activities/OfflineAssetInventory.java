package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetListAdapter;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.AssetInfoDialogUtil;
import com.test.inventorysystem.utils.InvAssetInfoDialogUtil;
import com.test.inventorysystem.utils.InvContinueDialogUtil;
import com.test.inventorysystem.utils.Sysconfig;
import com.test.inventorysystem.utils.TransUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfflineAssetInventory extends OrmLiteBaseActivity<DBHelper> implements InvAssetInfoDialogUtil.NoticeDialogListener, InvContinueDialogUtil.NoticeDialogListener {

    private final static int REQUEST_CODE = 1;
    private Button scanBtn = null;
    private Button manualBtn = null;
    private Button endBtn = null;
    private Spinner inventoryOrganSpinner = null;
    private ArrayAdapter<String> organSpinnerArrayAdapter = null;
    private ArrayList<OrganModel> organs = new ArrayList<OrganModel>();
    private ListView listView;
    private AssetListAdapter assetListAdapter;
    private LinearLayout inventoryProgressBar = null;
    private DBManager dbManager = new DBManager();

    private String currAssetCode;
    private String response;
    private AssetModel currAssetModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_asset_inventory);
        initialization();
    }

    private void initialization() {
        scanBtn = (Button) findViewById(R.id.button_offline_inventory_scan);
        manualBtn = (Button) findViewById(R.id.button_offline_inventory_manual);
        endBtn = (Button) findViewById(R.id.button_offline_inventory_end);
        inventoryProgressBar = (LinearLayout) findViewById(R.id.linearLayout_offline_progress_bar);

        inventoryOrganSpinner = (Spinner) findViewById(R.id.offline_spinner_inventory_organ);
        organSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        organSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryOrganSpinner.setAdapter(organSpinnerArrayAdapter);

        listView = (ListView) findViewById(R.id.listView_offline_asset_inventory);
        assetListAdapter = new AssetListAdapter(this, new ArrayList<AssetModel>());
        listView.setAdapter(assetListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AssetModel assetModel = (AssetModel) listView.getItemAtPosition(i);
                DialogFragment dialogFragment = AssetInfoDialogUtil.newInstace(assetModel, "offline");
                dialogFragment.show(getFragmentManager(), "inv_asset_update_info");
            }
        });

        try {
            List<OrganModel> organList = dbManager.findOrgans(this.getHelper().getOrganDao(), AppContext.currUser.getAccounts(), null);
            if (organList.isEmpty()) {

            } else {
                for (OrganModel organModel : organList) {
                    organs.add(organModel);
                    organSpinnerArrayAdapter.add(organModel.getOrganName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfflineAssetInventory.this, CaptureActivity.class);
                OfflineAssetInventory.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetMaunal();
            }
        });
    }

    private void startCodeScanner() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void assetMaunal() {
        Intent intent = new Intent(this, AssetManual.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CaptureActivity.RESULT_CODE) {
            Bundle bundle = data.getExtras();
            String barcode = bundle.getString("barcode");
            if (!"".equals(barcode)) {
                String[] codes = Sysconfig.getCodes(barcode);
                AssetModel assetModel = new AssetModel();
                String assetCode = codes[0];
                String assetName = codes[1];
                String operator = codes[2];
                String organName = codes[3];
                try {
                    dbManager.saveOfflineAsset(getHelper().getAssetDao(), assetModel, assetCode, assetName, operator, organName);
                    assetListAdapter.add(assetModel);
                    DialogFragment dialogFragment = new AssetInfoDialogUtil().newInstace(assetModel, "offline");
                    dialogFragment.show(getFragmentManager(), "inv_asset_info");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == InventoryUpdate.RESULT_CODE) {
            Bundle bundle = data.getExtras();
            AssetModel asset = (AssetModel) bundle.get("asset");
            if (null != asset) {
//                doInventory(asset.getAssetCode(), asset.getDisCode());
            }
        } else if (resultCode == AssetManual.RESULT_CODE) {
            Bundle bundle = data.getExtras();
            String code = bundle.getString("code");
            String name = bundle.getString("name");
            if (StringUtils.isNotBlank(code)) {
//                selectAssetInfo(code, "2");
            }
        }
    }

    //对应不同的dialog去判断他们的tag,以正确触发当前dialog的相应事件
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        System.out.println(dialog.getTag());
        switch (dialog.getTag()) {
            case "inv_asset_info":
//                doInventory(currAssetCode, "");
                break;
            case "continue":
                startCodeScanner();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        switch (dialog.getTag()) {
            case "inv_asset_info":
                updateInventory(currAssetModel);
                break;
            case "continue":
                break;
        }

    }

    private void updateInventory(AssetModel assetModel) {
        Intent intent = new Intent(this, InventoryUpdate.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("asset", assetModel);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
