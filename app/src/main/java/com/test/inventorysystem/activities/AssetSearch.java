
package com.test.inventorysystem.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.models.TypeModel;
import com.test.inventorysystem.utils.AppContext;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssetSearch extends OrmLiteBaseActivity<DBHelper> {

    private EditText assetName = null;
    private EditText assetCode = null;
    private Spinner assetOrganSpinner = null;
    private Spinner assetTypeSpinner = null;
    private Button searchBtn = null;
    private ArrayList<OrganModel> organs = new ArrayList<OrganModel>();
    private ArrayList<TypeModel> types = new ArrayList<TypeModel>();
    private ArrayAdapter<String> organSpinnerArrayAdapter = null;
    private ArrayAdapter<String> typeSpinnerArrayAdapter = null;
    private DBManager dbManager = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_search);
        Initialization();
    }

    private void Initialization () {
        assetName = (EditText) findViewById(R.id.editText_asset_name);
        assetCode = (EditText) findViewById(R.id.editText_asset_code);
        assetOrganSpinner = (Spinner) findViewById(R.id.spinner_asset_department);
        assetTypeSpinner = (Spinner) findViewById(R.id.spinner_asset_type);
        searchBtn = (Button) findViewById(R.id.button_asset_search);

        organSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        organSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assetOrganSpinner.setAdapter(organSpinnerArrayAdapter);

        typeSpinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        typeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assetTypeSpinner.setAdapter(typeSpinnerArrayAdapter);

//        Bundle extras = getIntent().getExtras();
//        String userAccount = extras.getString("userAccount");

        try {
            List<OrganModel> organList = dbManager.findOrgans(this.getHelper().getOrganDao(), AppContext.currUser.getAccounts(), null);
            if (organList.isEmpty()) {

            } else {
                for (int i = 0; i < organList.size(); i++) {
                    organs.add(organList.get(i));
                    organSpinnerArrayAdapter.add(organList.get(i).getOrganName());
                }
            }

            List<TypeModel> typeList = dbManager.findTypes(this.getHelper().getTypeDao(), AppContext.currUser.getAccounts(), "001");
            for (TypeModel typeModel : typeList) {
                types.add(typeModel);
                typeSpinnerArrayAdapter.add(typeModel.getTypeName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchList = new Intent(AssetSearch.this, AssetSearchList.class);
                searchList.putExtra("assetName", assetName.getText().toString().trim());
                searchList.putExtra("assetCode", assetCode.getText().toString().trim());
                searchList.putExtra("organCode", organs.get(assetOrganSpinner.getSelectedItemPosition()).getOrganCode());
                System.out.println("*** " + types.get(assetTypeSpinner.getSelectedItemPosition()).getTypeCode());
                searchList.putExtra("category", types.get(assetTypeSpinner.getSelectedItemPosition()).getTypeCode());
                startActivity(searchList);
            }
        });
    }
}
