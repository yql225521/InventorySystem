package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
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

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.DateFormat;
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

        inventoryOrganSpinner = (Spinner) findViewById(R.id.offline_spinner_inventory_organ);
        organSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        organSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryOrganSpinner.setAdapter(organSpinnerArrayAdapter);

        listView = (ListView) findViewById(R.id.listView_asset_inventory);
        assetListAdapter = new AssetListAdapter(this, new ArrayList<AssetModel>());
        listView.setAdapter(assetListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AssetModel assetModel = (AssetModel) listView.getItemAtPosition(i);
                DialogFragment dialogFragment = AssetInfoDialogUtil.newInstance(assetModel);
                dialogFragment.show(getFragmentManager(), "inv_asset_update_info");
            }
        });

        try {
            List<OrganModel> organList = dbManager.findOrgans(this.getHelper().getOrganDao(), AppContext.currUser.getAccounts(), null);
            if (organList.isEmpty()) {

            } else {
                for (int i = 0; i < organList.size(); i++) {
                    organs.add(organList.get(i));
                    organSpinnerArrayAdapter.add(organList.get(i).getOrganName());
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

        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inventorySearch();
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

    private void inventorySearch() {
        Intent intent = new Intent(this, InventorySearch.class);
        startActivity(intent);
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
                for (int i = 0; i < codes.length; i++) {
                    System.out.println(codes[i]);
                }
                selectAssetInfo(codes[0], "1");
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
                selectAssetInfo(code, "2");
            }
        }
    }

    private void selectAssetInfo(String finCode, final String pdfs) {
        inventoryProgressBar.setVisibility(LinearLayout.VISIBLE);
        String methodName = "getAssetInfoWithInv";
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("methodName", methodName);
        hashMap.put("finCode", finCode);
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
//                currAssetCode = asset.get("assetCode").getAsString();
                System.out.println("assetModel " + asset.toString());

                if (success == 1) {
                    Gson gson=new Gson();
                    currAssetModel = gson.fromJson(jsonObject.get("asset"), AssetModel.class);
//                    currAssetModel = new AssetModel(asset);
                    currAssetModel.setInvMsg(invMsg);
                    currAssetModel.setDisCodes("");
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
            case "inv_asset_info":
                doInventory(currAssetModel);
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

    public String getAssetJson(AssetModel assetModel){
        String json="";
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson g=gsonBuilder.create();
        json=g.toJson(assetModel);
        return json;
    }

    public void doInventory(AssetModel assetModel) {
        String methodName = "doInventory";
        HashMap<String, String> hashMap = new HashMap<String, String>();
        assetModel.setSimId(AppContext.simId);
        assetModel.setAddr(AppContext.address);
        assetModel.setMgrOrganCode(AppContext.currOrgan.getOrganCode());
        assetModel.setOrganCode(organs.get(inventoryOrganSpinner.getSelectedItemPosition()).getOrganCode());

//        hashMap.put("mgrOrganCode", AppContext.currOrgan.getOrganCode());
//        hashMap.put("assetCode", assetModel.getAssetCode());
//        hashMap.put("addr", AppContext.address);
//        hashMap.put("simId", AppContext.simId);
//        hashMap.put("disCodes", assetModel.getDisCodes());
//        hashMap.put("pdfs", assetModel.getPdfs());

        hashMap.put("methodName", methodName);
        hashMap.put("organCode", organs.get(inventoryOrganSpinner.getSelectedItemPosition()).getOrganCode());
        hashMap.put("username", AppContext.currUser.getAccounts());
        hashMap.put("assetJson", this.getAssetJson(assetModel));
        System.out.println("****" + this.getAssetJson(assetModel));
//        System.out.println(asset1);
//        hashMap.put("assetCode", assetCode);
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
                System.out.println("new response " + jsonObject);
                int success = jsonObject.get("success").getAsInt();
                String message = jsonObject.get("message").getAsString();
                JsonObject asset = jsonObject.get("asset").getAsJsonObject();

                AssetModel assetModel = new AssetModel(asset);

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
