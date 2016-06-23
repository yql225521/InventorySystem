package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetListAdapter;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AssetInfoDialogUtil;
import com.test.inventorysystem.utils.AssetQRDialogUtil;
import com.test.inventorysystem.utils.InvAssetInfoDialogUtil;
import com.test.inventorysystem.utils.Sysconfig;
import com.test.inventorysystem.utils.TransUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class AssetQRShow extends AppCompatActivity implements AssetQRDialogUtil.NoticeDialogListener {
    private final static int REQUEST_CODE = 1;
    private String response = "";
    private String currAssetCode;
    private AssetModel currAssetModel;

    private ListView listView;
    private AssetListAdapter assetListAdapter;
    private Button btnScan;
    private Button btnCancel;
    private LinearLayout inventoryProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_qrshow);
        initialization();
    }

    private void initialization() {
        btnScan = (Button) findViewById(R.id.button_qr_scan);
        btnCancel = (Button) findViewById(R.id.button_qr_cancel);
        inventoryProgressBar = (LinearLayout) findViewById(R.id.linearLayout_qr_progress_bar);

        listView = (ListView) findViewById(R.id.listView_qr_scan_list);
        assetListAdapter = new AssetListAdapter(this, new ArrayList<AssetModel>());
        listView.setAdapter(assetListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AssetModel assetModel = (AssetModel) listView.getItemAtPosition(i);
                DialogFragment dialogFragment = AssetInfoDialogUtil.newInstance(assetModel);
                dialogFragment.show(getFragmentManager(), "dialog_asset_info");
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCodeScanner();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void startCodeScanner() {
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
                System.out.println(jsonObject);

                int success = jsonObject.get("success").getAsInt();
                JsonObject asset = jsonObject.get("asset").getAsJsonObject();
                String invMsg = jsonObject.get("invMsg").getAsString();
                currAssetCode = asset.get("assetCode").getAsString();

//                AssetModel assetModel = new AssetModel(asset, "inv_asset");

                if (success == 1) {
                    currAssetModel = new AssetModel(asset, "inv_asset");
                    assetListAdapter.add(currAssetModel);
                    currAssetModel.setInvMsg(invMsg);
                    currAssetModel.setDisCode("");
                    currAssetModel.setPdfs(pdfs);
                    inventoryProgressBar.setVisibility(LinearLayout.GONE);
                    DialogFragment dialogFragment = AssetQRDialogUtil.newInstance(currAssetModel);
                    dialogFragment.show(getFragmentManager(), "asset_qr_show");
                }
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        switch (dialog.getTag()) {
            case "asset_qr_show":
                startCodeScanner();
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
