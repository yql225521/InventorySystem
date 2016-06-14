package com.test.inventorysystem.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.models.OrganModel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetInventory extends OrmLiteBaseActivity<DBHelper> {

    private Button scanBtn = null;
    private Button inventoryBtn = null;
    private Button manualBtn = null;
    private Button endBtn = null;
    private Spinner inventoryOrganSpinner = null;
    private ArrayAdapter<String> organSpinnerArrayAdapter = null;
    private ArrayList<OrganModel> organs = new ArrayList<OrganModel>();
    private DBManager dbManager = new DBManager();

    private String userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_inventory);
        Initialization();
    }

    private void Initialization () {
        scanBtn = (Button) findViewById(R.id.button_inventory_scan);
        inventoryBtn = (Button) findViewById(R.id.button_inventory_inventory);
        manualBtn = (Button) findViewById(R.id.button_inventory_manual);
        endBtn = (Button) findViewById(R.id.button_inventory_end);

        inventoryOrganSpinner = (Spinner) findViewById(R.id.spinner_inventory_organ);
        organSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        organSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inventoryOrganSpinner.setAdapter(organSpinnerArrayAdapter);

        Bundle extras = getIntent().getExtras();
        userAccount = extras.getString("userAccount");

        try {
            List<OrganModel> organList = dbManager.findOrgans(this.getHelper().getOrganDao(), userAccount, null);
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
                startQRcodeScanner();
            }
        });
    }

    private void startQRcodeScanner() {

    }
}
