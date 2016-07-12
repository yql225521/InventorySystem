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

public class InventorySearch extends OrmLiteBaseActivity<DBHelper> {

    private Spinner spinnerOrgan;
    private Spinner spinnerType;
    private Spinner spinnerStatus;
    private Spinner spinnerMatch;
    private ArrayAdapter<String> spinnerOrganAdapter;
    private ArrayAdapter<String> spinnerTypeAdapter;
    private ArrayAdapter<String> spinnerStatusAdapter;
    private ArrayAdapter<String> spinnerMatchAdapter;
    private ArrayList<OrganModel> organs;
    private ArrayList<TypeModel> types;
    private ArrayList<TypeModel> status;
    private ArrayList<TypeModel> matches;
    private EditText editTextPlace;
    private Button btnSearch;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_records);
        initialization();
    }

    private void initialization() {
        spinnerOrgan = (Spinner) findViewById(R.id.spinner_inv_records_organ);
        spinnerOrganAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerOrganAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrgan.setAdapter(spinnerOrganAdapter);

        spinnerType = (Spinner) findViewById(R.id.spinner_inv_records_type);
        spinnerTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerTypeAdapter);

        spinnerStatus = (Spinner) findViewById(R.id.spinner_inv_records_status);
        spinnerStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerStatusAdapter);

        spinnerMatch = (Spinner) findViewById(R.id.spinner_inv_records_match);
        spinnerMatchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerMatchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMatch.setAdapter(spinnerMatchAdapter);

        editTextPlace = (EditText) findViewById(R.id.editText_inv_records_place);
        btnSearch = (Button) findViewById(R.id.button_inv_records_search);

        organs = new ArrayList<>();
        types = new ArrayList<>();
        status = new ArrayList<>();
        matches = new ArrayList<>();
        dbManager = new DBManager();
        try {
            List<OrganModel> organList = dbManager.findOrgans(this.getHelper().getOrganDao(), AppContext.currUser.getAccounts(), null);
            if (organList.isEmpty()) {

            } else {
                for (int i = 1; i < organList.size(); i++) {
                    organs.add(organList.get(i));
                    spinnerOrganAdapter.add(organList.get(i).getOrganName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            List<TypeModel> typeList = dbManager.findTypes(this.getHelper().getTypeDao(), AppContext.currUser.getAccounts(), "001");
            if (typeList.isEmpty()) {

            } else {
                for (TypeModel typeModel : typeList) {
                    types.add(typeModel);
                    spinnerTypeAdapter.add(typeModel.getTypeName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            List<TypeModel> statusList = dbManager.findTypes(this.getHelper().getTypeDao(), AppContext.currUser.getAccounts(), "V01");
            if (statusList.isEmpty()) {

            } else {
                for (TypeModel typeModel : statusList) {
                    status.add(typeModel);
                    spinnerStatusAdapter.add(typeModel.getTypeName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            List<TypeModel> matchList = dbManager.findTypes(this.getHelper().getTypeDao(), AppContext.currUser.getAccounts(), "V02");
            if (matchList.isEmpty()) {

            } else {
                for (TypeModel typeModel : matchList) {
                    matches.add(typeModel);
                    spinnerMatchAdapter.add(typeModel.getTypeName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInvRecords();
            }
        });
    }

    private void searchInvRecords() {
        Intent intent = new Intent(this, InventorySearchList.class);
        Bundle bundle = new Bundle();

        bundle.putString("storage", editTextPlace.getText().toString().trim());
        bundle.putString("organCode", organs.get(spinnerOrgan.getSelectedItemPosition()).getOrganCode());
        bundle.putString("category", types.get(spinnerType.getSelectedItemPosition()).getTypeCode());
        bundle.putString("storageMatchType", matches.get(spinnerMatch.getSelectedItemPosition()).getTypeCode());
        bundle.putString("complete", status.get(spinnerStatus.getSelectedItemPosition()).getTypeCode());

        intent.putExtras(bundle);
        startActivity(intent);
    }
}
