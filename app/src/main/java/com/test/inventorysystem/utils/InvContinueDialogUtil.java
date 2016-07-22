package com.test.inventorysystem.utils;

import android.app.Activity;
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

/**
 * Created by youmengli on 6/20/16.
 */

public class InvContinueDialogUtil extends DialogFragment {
    private TextView inventoryMsg;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    public static InvContinueDialogUtil newInstance(String msg) {
        InvContinueDialogUtil frag = new InvContinueDialogUtil();
        Bundle args = new Bundle();
        args.putString("msg", msg);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString("msg");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_inv_asset_continue, null);
        initialization(view);

        inventoryMsg.setText(msg);
        builder.setView(view)
                .setPositiveButton(R.string.continue_dialog_continue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogPositiveClick(InvContinueDialogUtil.this);
                    }
                })
                .setNegativeButton(R.string.continue_dialog_stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(InvContinueDialogUtil.this);
                    }
                });
        builder.setCancelable(false);
        return builder.create();
    }

    private void initialization(View view) {
        inventoryMsg = (TextView) view.findViewById(R.id.textView_continue_dialog);
    }
}
