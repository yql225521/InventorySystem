package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.stmt.query.In;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetListAdapter;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.InvAssetInfoDialogUtil;
import com.test.inventorysystem.utils.InvContinueDialogUtil;
import com.test.inventorysystem.utils.Sysconfig;
import com.test.inventorysystem.utils.TransUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssetInventory extends OrmLiteBaseActivity<DBHelper> implements InvAssetInfoDialogUtil.NoticeDialogListener, InvContinueDialogUtil.NoticeDialogListener {
    private final static int REQUEST_CODE = 1;
    private Button scanBtn = null;
    private Button inventoryBtn = null;
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
        setContentView(R.layout.activity_asset_inventory);
        initialization();
    }

    private void initialization() {
        scanBtn = (Button) findViewById(R.id.button_inventory_scan);
        inventoryBtn = (Button) findViewById(R.id.button_inventory_inventory);
        manualBtn = (Button) findViewById(R.id.button_inventory_manual);
        endBtn = (Button) findViewById(R.id.button_inventory_end);
        inventoryProgressBar = (LinearLayout) findViewById(R.id.linearLayout_progress_bar);

        inventoryOrganSpinner = (Spinner) findViewById(R.id.spinner_inventory_organ);
        organSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        organSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryOrganSpinner.setAdapter(organSpinnerArrayAdapter);

        listView = (ListView) findViewById(R.id.listView_asset_inventory);
        assetListAdapter = new AssetListAdapter(this, new ArrayList<AssetModel>());
        listView.setAdapter(assetListAdapter);

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
            public void onClick(View view) {
                startCodeScanner();
            }
        });
    }

    private void startCodeScanner() {
        System.out.println("开始扫描....");
        Intent intent = new Intent(this, CaptureActivity.class);
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
                selectAssetInfo(codes[0], "1");
            }
        }
//        } else if (resultCode==AssetManual.RESULT_CODE){
//            Bundle bundle=data.getExtras();
//            String code=bundle.getString("code");
//            String name=bundle.getString("name");
//            if (StringUtils.isNotBlank(code)) {
//                selectAssetInfo(code,"2");
//            }
//        }else if (resultCode==InventoryChgSel.RESULT_CODE){
//            Bundle bundle=data.getExtras();
//            AssetEntity asset=(AssetEntity)bundle.get("asset");
//            if (null!=asset) {
//                doInventory(asset);
//                //assetListViewAdapter.notifyDataSetChanged();
//            }
//        }
    }

    private void selectAssetInfo(String assetCode, final String pdfs) {
        inventoryProgressBar.setVisibility(LinearLayout.VISIBLE);
        String methodName = "getAssetInfoWithInv";
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("methodName", methodName);
        hashMap.put("assetCode", assetCode);
        loadAssetInfo(hashMap, pdfs);
    }

    private void loadAssetInfo(HashMap hashMap, final String pdfs) {
        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                response = TransUtil.decode(sa.getResponse());
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(response);

                int success = jsonObject.get("success").getAsInt();
                JsonObject asset = jsonObject.get("asset").getAsJsonObject();
                String invMsg = jsonObject.get("invMsg").getAsString();
                currAssetCode = asset.get("assetCode").getAsString();

                if (success == 1) {
                    currAssetModel = new AssetModel(asset, "inv_asset");
                    currAssetModel.setInvMsg(invMsg);
                    currAssetModel.setDisCode("");
                    currAssetModel.setPdfs(pdfs);
                    inventoryProgressBar.setVisibility(LinearLayout.GONE);
                    DialogFragment dialogFragment = InvAssetInfoDialogUtil.newInstance(currAssetModel);
                    dialogFragment.show(getFragmentManager(), "inv_asset_info");
                }
            }
        });
    }

    //对应不同的dialog去判断他们的tag,以正确触发当前dialog的相应事件
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        System.out.println(dialog.getTag());
        switch (dialog.getTag()) {
            case "inv_asset_info" :
                doInventory(currAssetCode, "");
                break;
            case "continue" :
                startCodeScanner();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        switch (dialog.getTag()) {
            case "inv_asset_info" :
                updateInventory(currAssetModel);
                break;
            case "continue" :
                break;
        }

    }

    public void doInventory(String assetCode, String disCodes) {
        String methodName = "doInventory";
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("methodName", methodName);
        hashMap.put("organCode", organs.get(inventoryOrganSpinner.getSelectedItemPosition()).getOrganCode());
        hashMap.put("mgrOrganCode", AppContext.currOrgan.getOrganCode());
        hashMap.put("username", AppContext.currUser.getAccounts());
        hashMap.put("assetCode", assetCode);
        hashMap.put("addr", AppContext.address);
        hashMap.put("simId", AppContext.simId);
        hashMap.put("disCodes", disCodes);
        loadDoInventoryInfo(hashMap);
    }

    private void loadDoInventoryInfo(HashMap hashMap) {
        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                response = TransUtil.decode(sa.getResponse());
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
                System.out.println("DoInventory back msg : " + jsonObject);

                int success = jsonObject.get("success").getAsInt();
                String message = jsonObject.get("message").getAsString();
                JsonObject asset = jsonObject.get("asset").getAsJsonObject();

                AssetModel assetModel = new AssetModel(asset, "inv_asset");

                if (success == 1) {
                    assetListAdapter.replace(assetModel);
                    message = "[" + assetModel.getAssetCode() + "] " + assetModel.getAssetName() + "盘点成功";
                    DialogFragment dialogFragment = InvContinueDialogUtil.newInstance(message);
                    dialogFragment.show(getFragmentManager(), "continue");
                }

            }
        });
    }

    private void updateInventory(AssetModel assetModel) {
        Intent intent = new Intent(this, InventoryUpdate.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("asset", assetModel);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE);
    }
}
