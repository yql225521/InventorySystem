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
import com.test.inventorysystem.adapters.OfflineInvListAdapter;
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
import com.test.inventorysystem.utils.OfflineInvExistedAssetDialogUtil;
import com.test.inventorysystem.utils.Sysconfig;
import com.test.inventorysystem.utils.TransUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfflineAssetInventory extends OrmLiteBaseActivity<DBHelper> implements InvAssetInfoDialogUtil.NoticeDialogListener, OfflineInvExistedAssetDialogUtil.NoticeDialogListener {

    private final static int REQUEST_CODE = 1;
    private Button scanBtn = null;
    private Button manualBtn = null;
    private Button endBtn = null;
    private Spinner inventoryOrganSpinner = null;
    private ArrayAdapter<String> organSpinnerArrayAdapter;
    private ArrayList<OrganModel> organs = new ArrayList<OrganModel>();
    private ListView listView;
//    private AssetListAdapter assetListAdapter;
    private OfflineInvListAdapter assetListAdapter = null;
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
        organSpinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        organSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryOrganSpinner.setAdapter(organSpinnerArrayAdapter);
        inventoryOrganSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                assetListAdapter.clear();
                try {
                    List<AssetModel> list = dbManager.findOfflineInvAssets(getHelper().getAssetDao(), organs.get(inventoryOrganSpinner.getSelectedItemPosition()).getOrganCode(), true);
                    System.out.println("test");
                    assetListAdapter.addAll(list);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView = (ListView) findViewById(R.id.listView_offline_asset_inventory);
        assetListAdapter = new OfflineInvListAdapter(this, new ArrayList<AssetModel>());
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
                startCodeScanner();
            }
        });

        manualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assetMaunal();
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endOfflineInventory();
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

    private void endOfflineInventory() {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CaptureActivity.RESULT_CODE) {
            Bundle bundle = data.getExtras();
            String barcode = bundle.getString("barcode");
            if (!"".equals(barcode)) {
                String[] codes = Sysconfig.getCodes(barcode);
                currAssetModel = new AssetModel();
                try {
                    Boolean isExisted = dbManager.findExistedOfflineInvAsset(getHelper().getAssetDao(), codes[0]);
                    String msg = "该资产已经离线盘点";
                    System.out.println(isExisted);
                    if (isExisted) {
                        DialogFragment dialogFragment = new OfflineInvExistedAssetDialogUtil().newInstance(msg);
                        dialogFragment.show(getFragmentManager(), "inv_offline_existed_asset");
                    } else {
                        currAssetModel.setAssetCode(codes[0]);
                        currAssetModel.setAssetName(codes[1]);
                        currAssetModel.setOrganName(codes[2].substring(2, codes[2].length()));
                        currAssetModel.setMgrOrganCode(AppContext.currOrgan.getOrganCode());
                        currAssetModel.setOrganCode(organs.get(inventoryOrganSpinner.getSelectedItemPosition()).getOrganCode());
                        currAssetModel.setUserId(AppContext.currUser.getAccounts());

                        DialogFragment dialogFragment = new InvAssetInfoDialogUtil().newInstance(currAssetModel, "offline");
                        dialogFragment.show(getFragmentManager(), "inv_offline_asset_info");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == InventoryUpdate.RESULT_CODE) {
            Bundle bundle = data.getExtras();
            AssetModel asset = (AssetModel) bundle.get("asset");
            if (null != asset) {
                doInventory(asset);
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
            case "inv_offline_asset_info":
                doInventory(currAssetModel);
                break;
            case "inv_offline_existed_asset":
                startCodeScanner();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        switch (dialog.getTag()) {
            case "inv_offline_asset_info":
                updateInventory(currAssetModel);
                break;
            case "inv_offline_existed_asset":
                replaceDuplicate(currAssetModel);
                break;
        }

    }

    private void doInventory(AssetModel assetModel) {
        try {
            dbManager.saveOfflineInvAssets(getHelper().getAssetDao(), assetModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assetListAdapter.add(assetModel);
    }

    private void replaceDuplicate(AssetModel assetModel) {
        try {
            dbManager.saveOfflineInvAssets(getHelper().getAssetDao(), assetModel);
        } catch (SQLException e) {
            e.printStackTrace();
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
