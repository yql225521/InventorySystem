package com.test.inventorysystem.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.inventorysystem.R;

public class AssetManual extends AppCompatActivity {

    public final static int RESULT_CODE=3;
    private EditText editTextCode;
    private EditText editTextName;
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
        editTextName = (EditText) findViewById(R.id.editText_asset_manual_name);
        btnConfirm = (Button) findViewById(R.id.button_asset_manual_confirm);
        btnCancel = (Button) findViewById(R.id.button_asset_manual_cancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextCode.getText().toString().trim().equals("")) {
                    Toast.makeText(AssetManual.this, "请输入资产编码...", Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (editTextName.getText().toString().trim().equals("")) {
//                    Toast.makeText(AssetManual.this, "请输入资产名称...", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intent = new Intent();
                intent.putExtra("code", editTextCode.getText().toString().trim());
                intent.putExtra("name", editTextName.getText().toString().trim());
                setResult(RESULT_CODE, intent);
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
