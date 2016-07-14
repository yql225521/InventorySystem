package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetListAdapter;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AssetInfoDialogUtil;
import com.test.inventorysystem.utils.TransUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class InventorySearchList extends OrmLiteBaseActivity<DBHelper> {

    private int pageSize = 15;
    private int currPageIndex = 0;
    private int recordCount = 0;
    private int totalCount = 0;
    private String response = "";

    private HashMap<String, String> hashMap;
    private ListView listView;
    private AssetListAdapter listAdapter;
    private ArrayList<AssetModel> assetList;
    private TextView countInfo;
    private LinearLayout mProgressBar;

    private boolean isLoading = false;
    private boolean isFirstLoad = true;
    private boolean endLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_search_list);
        initialization();
    }

    private void initialization() {
        Bundle bundle = getIntent().getExtras();
        hashMap = new HashMap<>();
        hashMap.put("methodName", "searchInventoryAssetList");
        hashMap.put("organCode", bundle.getString("organCode"));
        hashMap.put("category", bundle.getString("category"));
        hashMap.put("storage", bundle.getString("storage"));
        hashMap.put("complete", bundle.getString("complete"));
        hashMap.put("storageMatchType", bundle.getString("storageMatchType"));

        mProgressBar = (LinearLayout) findViewById(R.id.inv_search_list_progress_layout);
        countInfo = (TextView) findViewById(R.id.textView_inv_search_list_count);
        listView = (ListView) findViewById(R.id.inv_search_list_view);
        assetList = new ArrayList<>();
        listAdapter = new AssetListAdapter(this, assetList);
        listView.setAdapter(listAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount && !isFirstLoad && !isLoading && !endLoading) {
                    isLoading = true;
                    loadMoreAssets(hashMap);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AssetModel assetModel = (AssetModel) listView.getItemAtPosition(i);
                DialogFragment dialogFragment = AssetInfoDialogUtil.newInstance(assetModel);
                dialogFragment.show(getFragmentManager(), "dialog_asset_info");
            }
        });
        loadMoreAssets(hashMap);
    }

    private void loadMoreAssets(HashMap<String, String> hashMap) {
        mProgressBar.setVisibility(LinearLayout.VISIBLE);
        currPageIndex++;
        hashMap.put("pageSize", String.valueOf(pageSize));
        hashMap.put("pageNo", String.valueOf(currPageIndex));

        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                response = TransUtil.decode(sa.getResponse());
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
                int success = jsonObject.get("success").getAsInt();
                JsonArray assetListString = jsonObject.get("list").getAsJsonArray();
                recordCount = jsonObject.get("recordcount").getAsInt();
                if (success == 1) {
                    if (assetListString.size() != 0) {
                        assetList.clear();
                        for (int i = 0; i < assetListString.size(); i++) {
                            AssetModel assetModel = new AssetModel(assetListString.get(i).getAsJsonObject());
                            assetList.add(assetModel);
                        }
                        listAdapter.addAll(assetList);
                        if (isFirstLoad) {
                            isFirstLoad = false;
                        }
                        mProgressBar.setVisibility(LinearLayout.GONE);
                        isLoading = false;
                        totalCount = InventorySearchList.this.getListAdapter().getCount();
                        countInfo.setText("已加载" + totalCount + "条-共" + recordCount + "条");
                    } else {
                        mProgressBar.setVisibility(LinearLayout.GONE);
                        endLoading = true;
                        Toast.makeText(InventorySearchList.this, "没有更多数据了...", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private AssetListAdapter getListAdapter() {
        return this.listAdapter;
    }
}
