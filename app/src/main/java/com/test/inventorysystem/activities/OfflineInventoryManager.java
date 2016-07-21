package com.test.inventorysystem.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.OfflineInvListMgrAdapter;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.TransUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class OfflineInventoryManager extends OrmLiteBaseActivity<DBHelper> {

    private Button btnUpload;
    private Button btnDelete;
    private Button btnCheckAll;
    private Button btnCheckReverse;
    private Button btnCancel;
    private ListView listView;
    private LinearLayout progressBar;
    private OfflineInvListMgrAdapter offlineInvListMgrAdapter;

    private DBManager dbManager = new DBManager();
    private ArrayList<OrganModel> organList = new ArrayList<>();
    private ArrayList<OrganModel> organUploadList = new ArrayList<>(); // 所有离线判断数据部门列表
    private ArrayList<OrganModel> organUploadQueue = new ArrayList<>(); // 当前选中的盘点数据部门, 待上传或者删除
    private ArrayList<List> offlineInvAssetList = new ArrayList<>();

    private Boolean uploadStatus = false;
    private String uploadMsg = "";
    private String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_inventory_manager);
        initialization();
    }

    private void initialization() {
        btnUpload = (Button) findViewById(R.id.button_offline_inv_upload);
        btnDelete = (Button) findViewById(R.id.button_offline_inv_delete);
        btnCheckAll = (Button) findViewById(R.id.button_offline_inv_check_all);
        btnCheckReverse = (Button) findViewById(R.id.button_offline_inv_check_reverse);
        btnCancel = (Button) findViewById(R.id.button_offline_inv_cancel);
        progressBar = (LinearLayout) findViewById(R.id.offline_inv_mgr_progress_layout);

        try {
            List<OrganModel> list = dbManager.findOrgans(this.getHelper().getOrganDao(), AppContext.currUser.getAccounts(), null);
            for (int i = 0; i < list.size(); i++) {
                organList.add(list.get(i));
            }
            for (int i = 0; i < organList.size(); i++) {
                List<AssetModel> assetListWithOrgan = dbManager.findOfflineInvAssetsByOrgan(this.getHelper().getAssetDao(), organList.get(i).getOrganCode(), true);
                if (assetListWithOrgan.size() != 0) {
                    organUploadList.add(organList.get(i));
                    offlineInvAssetList.add(assetListWithOrgan);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        listView = (ListView) findViewById(R.id.listView_offline_inv);
        offlineInvListMgrAdapter = new OfflineInvListMgrAdapter(this, organUploadList, offlineInvAssetList);
        listView.setAdapter(offlineInvListMgrAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("heheda");
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCheckedItems();
            }
        });

        btnCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAll();
            }
        });

        btnCheckReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reverse();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkAll() {
        offlineInvListMgrAdapter.checkAll();
    }

    private void reverse() {
        offlineInvListMgrAdapter.reverse();
    }

    private void deleteCheckedItems() {
        organUploadQueue = offlineInvListMgrAdapter.getUploadOrganQueue();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                for (int i = 0; i < organUploadQueue.size(); i++) {
                    try {
                        dbManager.deleteOfflineInvAssetsWithOrgan(getHelper().getAssetDao(), AppContext.currUser.getAccounts(), organUploadQueue.get(i).getOrganCode());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                offlineInvListMgrAdapter.remove();
                offlineInvListMgrAdapter.clear();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        if (organUploadQueue.isEmpty()) {
            Toast.makeText(this, "请选择要删除数据的部门", Toast.LENGTH_SHORT).show();
        } else {
            builder.setMessage("确定要删除已选盘点数据?...")
                    .setTitle(R.string.offline_asset_inventory_delete);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void upload() {
        if (AppContext.offlineLogin) {
            Toast.makeText(OfflineInventoryManager.this, "离线登录不能上传数据...", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            organUploadQueue = offlineInvListMgrAdapter.getUploadOrganQueue();
            if (!organUploadQueue.isEmpty()) {
                uploadStatus = true;
                uploadMsg = "";
                uploadData(organUploadQueue);
                System.out.println("upload..." + organUploadQueue);
            }
        }
    }

    private void uploadData(ArrayList<OrganModel> organUpladQueue) {
        Iterator<OrganModel> iterator = organUpladQueue.iterator();

        if (!iterator.hasNext()) {
            offlineInvListMgrAdapter.clear();
            offlineInvListMgrAdapter.notifyDataSetChanged();
            uploadStatus = false;
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "离线数据上传完成", Toast.LENGTH_SHORT).show();
            return;
        }

        OrganModel organ = iterator.next();
        doUploadInventory(iterator, organ);
    }

    private void doUploadInventory(final Iterator<OrganModel> iterator, final OrganModel organ) {
        try {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("methodName", "doUpLoadInventory");
            hashMap.put("username", AppContext.currUser.getAccounts());
            hashMap.put("organCode", AppContext.currOrgan.getOrganCode());
            System.out.println("brefore upload " + getUploadData(organ.getOrganCode()));
            hashMap.put("assetJson", TransUtil.encode(getUploadData(organ.getOrganCode())));

            final SOAPActions sa = new SOAPActions(hashMap);
            String xmlRequest = sa.getXmlRequest();

            sa.sendRequest(this, xmlRequest, new CallbackInterface() {
                @Override
                public void callBackFunction() {
                    response = TransUtil.decode(sa.getResponse().toString());
                    response = response.replace("&quot;", "\"");
                    JsonParser jsonParser = new JsonParser();
                    System.out.println(response);
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
                    String success = jsonObject.get("success").toString();

                    if (success.equals("1")) {
                        offlineInvListMgrAdapter.remove();
                        iterator.remove();
                        try {
                            dbManager.deleteOfflineInvAssetsWithOrgan(getHelper().getAssetDao(), AppContext.currUser.getAccounts(), organ.getOrganCode());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadData(organUploadQueue);
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUploadData(String organCode) throws SQLException {
        String json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson g = gsonBuilder.create();
        List<AssetModel> datlst = dbManager.findOfflineInvAssetsByOrgan(this.getHelper().getAssetDao(), organCode, true);
        json = g.toJson(datlst);
        return json;
    }
}
