package com.test.inventorysystem.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetUpdateListAdapter;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.utils.AppContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfflineInventoryManager extends OrmLiteBaseActivity<DBHelper> {

    private Button btnUpload;
    private Button btnDelete;
    private Button btnCheckAll;
    private Button btnCheckReverse;
    private Button btnCancel;
    private ListView listView;

    private DBManager dbManager;
    private ArrayList<OrganModel> organList;
    private ArrayList<List> offlineInvAsset;

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

        organList = new ArrayList<>();
        dbManager = new DBManager();
        try {
            List<OrganModel> list = dbManager.findOrgans(this.getHelper().getOrganDao(), AppContext.currUser.getAccounts(), null);
            for (int i = 1; i < list.size(); i++) {
                organList.add(list.get(i));
            }
                System.out.println(organList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listView = (ListView) findViewById(R.id.listView_offline_inv);
//        AssetUpdateListAdapter assetUpdateListAdapter = new AssetUpdateListAdapter(this, titles, info);
     }
}
