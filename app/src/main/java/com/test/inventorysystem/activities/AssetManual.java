package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.ExtDate;
import com.test.inventorysystem.utils.OfflineInvExistedAssetDialogUtil;

import java.sql.SQLException;

public class AssetManual extends AppCompatActivity {

    public final static int RESULT_CODE = 3;
//    public final static int RESULT_FIN_CODE = 4;
    private EditText editTextCode;
    private EditText editTextFinCode;
    private Button btnConfirm;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_manual);
        initialization();
    }

    private void initialization() {
        editTextCode = (EditText) findViewById(R.id.editText_asset_manual_code);
        editTextFinCode = (EditText) findViewById(R.id.editText_asset_manual_fincode);
        btnConfirm = (Button) findViewById(R.id.button_asset_manual_confirm);
        btnCancel = (Button) findViewById(R.id.button_asset_manual_cancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextCode.getText().toString().trim().equals("") && editTextFinCode.getText().toString().trim().equals("")) {
                    Toast.makeText(AssetManual.this, "请输入资产或者财务编码", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!editTextCode.getText().toString().trim().equals("") && !editTextFinCode.getText().toString().trim().equals("")) {
                    Toast.makeText(AssetManual.this, "请勿同时输入两种编码", Toast.LENGTH_LONG).show();
                    return;
                }

//                String assetCode = String.valueOf(editTextCode.getText());
//                String finCode = String.valueOf(editTextFinCode.getText());

                Intent intent = new Intent();
                if (!editTextCode.getText().toString().trim().equals("")) {
                    intent.putExtra("code", editTextCode.getText().toString().trim());
                    setResult(RESULT_CODE, intent);
                } else {
                    intent.putExtra("code", editTextFinCode.getText().toString().trim());
                    setResult(RESULT_CODE, intent);
                }
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
