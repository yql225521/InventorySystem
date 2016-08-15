package com.test.inventorysystem.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.models.AssetModel;

import org.w3c.dom.Text;

/**
 * Created by youmengli on 6/13/16.
 */

public class AssetInfoDialogUtil extends DialogFragment {

    private TextView assetTitle;
    private TextView assetCode;
    private TextView assetFinCode;
    private TextView assetName;
    //    private TextView assetSpecification;
    private TextView assetType;
    private TextView assetCategory;
    private TextView assetMgrOrgan;
    private TextView assetOrgan;
    private TextView assetPlace;
    private TextView assetOperator;
    private TextView assetOriginalValue;
    private TextView assetDate;
    private TextView assetUseAge;
    private TextView assetStatus;

    public static AssetInfoDialogUtil newInstance(AssetModel assetModel) {
        AssetInfoDialogUtil frag = new AssetInfoDialogUtil();
        Bundle args = new Bundle();
        if (assetModel.getAssetCode() != null) {
            args.putString("code", assetModel.getAssetCode());
        }
        if (assetModel.getFinCode() != null){
            args.putString("finCode", assetModel.getFinCode());
        }
        if (assetModel.getAssetName() != null) {
            args.putString("name", assetModel.getAssetName());
        }
        if (assetModel.getAssetTypeName() != null) {
            args.putString("type", assetModel.getAssetTypeName());
        }
        if (assetModel.getCateName() != null) {
            args.putString("category", assetModel.getCateName());
        }
        if (assetModel.getMgrOrganName() != null) {
            args.putString("mgr_organ", assetModel.getMgrOrganName());
        }
        if (assetModel.getOrganName() != null) {
            args.putString("organ", assetModel.getOrganName());
        }
        if (assetModel.getStorageDescr() != null) {
            args.putString("place", assetModel.getStorageDescr());
        }
        if (assetModel.getOperator() != null) {
            args.putString("operator", assetModel.getOperator());
        }
        if (assetModel.getOriginalValue() != null) {
            args.putDouble("original_value", assetModel.getOriginalValue());
        }
        if (assetModel.getEnableDateString() != null) {
            args.putString("date", assetModel.getEnableDateString());
        }
        if (assetModel.getUseAge() != null) {
            args.putString("use_age", assetModel.getUseAge());
        }
        if (assetModel.getStatus() != null) {
            args.putString("status", assetModel.getStatus());
        }
        frag.setArguments(args);
        return frag;
    }

    public static AssetInfoDialogUtil newInstace(AssetModel assetModel, String offline) {
        AssetInfoDialogUtil frag = new AssetInfoDialogUtil();
        Bundle args = new Bundle();
        args.putString("code", assetModel.getAssetCode());
        args.putString("name", assetModel.getAssetName());
        args.putString("organ", assetModel.getOrganName());
//        args.putString("operator", assetModel.getOperator());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String asset_code = getArguments().getString("code");
        String asset_finCode = getArguments().getString("finCode");
        String asset_name = getArguments().getString("name");
        String asset_type = getArguments().getString("type");
        String asset_category = getArguments().getString("category");
        String asset_mgr_organ = getArguments().getString("mgr_organ");
        String asset_organ = getArguments().getString("organ");
        String asset_place = getArguments().getString("place");
        String asset_operator = getArguments().getString("operator");
        double asset_original_value = getArguments().getDouble("original_value");
        String asset_date = getArguments().getString("date");
        String asset_use_age = getArguments().getString("use_age");
        String asset_status = getArguments().getString("status");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_asset_info, null);
        Initialization(view);

        assetTitle.setText(asset_name);
        assetCode.setText(asset_code);
        assetFinCode.setText(asset_finCode);
        assetName.setText(asset_name);
        assetType.setText(asset_type);
        assetCategory.setText(asset_category);
        assetMgrOrgan.setText(asset_mgr_organ);
        assetOrgan.setText(asset_organ);
        assetPlace.setText(asset_place);
        assetOperator.setText(asset_operator);
        if (asset_original_value != 0) {
            assetOriginalValue.setText(String.valueOf(asset_original_value));
        }
        assetDate.setText(asset_date);
        assetUseAge.setText(asset_use_age);
        assetStatus.setText(asset_status);

        builder.setView(view)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.setCancelable(false);
        return builder.create();
    }

    private void Initialization(View view) {
        assetTitle = (TextView) view.findViewById(R.id.textView_asset_info_title);
        assetCode = (TextView) view.findViewById(R.id.textView_asset_info_code);
        assetFinCode = (TextView) view.findViewById(R.id.textView_asset_info_fincode);
        assetName = (TextView) view.findViewById(R.id.textView_asset_info_name);
        assetType = (TextView) view.findViewById(R.id.textView_asset_info_type);
        assetCategory = (TextView) view.findViewById(R.id.textView_asset_info_category);
        assetMgrOrgan = (TextView) view.findViewById(R.id.textView_asset_info_mgr_organ_name);
        assetOrgan = (TextView) view.findViewById(R.id.textView_asset_info_organ_name);
        assetPlace = (TextView) view.findViewById(R.id.textView_asset_info_place);
        assetOperator = (TextView) view.findViewById(R.id.textView_asset_info_operator);
        assetOriginalValue = (TextView) view.findViewById(R.id.textView_asset_info_original_value);
        assetDate = (TextView) view.findViewById(R.id.textView_asset_info_date);
        assetUseAge = (TextView) view.findViewById(R.id.textView_asset_info_use_age);
        assetStatus = (TextView) view.findViewById(R.id.textView_asset_info_status);
    }
}
