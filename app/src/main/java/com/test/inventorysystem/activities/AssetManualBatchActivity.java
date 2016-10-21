package com.test.inventorysystem.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.AssetListAdapter;
import com.test.inventorysystem.adapters.AssetManualBatchListAdapter;
import com.test.inventorysystem.adapters.AssetUpdateListAdapter;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.ExtDate;
import com.test.inventorysystem.utils.InvContinueDialogUtil;
import com.test.inventorysystem.utils.TransUtil;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

/**
 *离线和在线集成  手工录入，批量确认
 */
public class AssetManualBatchActivity extends OrmLiteBaseActivity<DBHelper> {

    public final static int RESULT_CODE=0x3;

    //定义组件
    TextView tv_countInfo,tv_selectInfo;
    EditText edit_assetid;
    Button btn_asset_query,btn_asset_ok,btn_asset_cancel;
    ListView listView_asset_list;

    private int pageSize = 15;
    private int currPageIndex = 0;

    private int recordCount = 0;
    private int totalCount = 0;

    private String assetOrgan="";
    private String response = "";
    private HashMap<String, String> hashMap;

    AssetManualBatchListAdapter assetManualBatchListAdapter;
    AssetModel asset;
    ArrayList<AssetModel> assetList=new ArrayList<>();

    private DBManager dbManager = new DBManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_manual_batch);
        initialization();
    }

    private void initialization() {

        //获取使用部门organ_code
        Intent intent=getIntent();
        assetOrgan=intent.getStringExtra("organCode");

        edit_assetid= (EditText) findViewById(R.id.edit_aseetid);
        btn_asset_query= (Button) findViewById(R.id.btn_query);
        btn_asset_ok= (Button) findViewById(R.id.btn_ok);
        tv_countInfo= (TextView) findViewById(R.id.tv_countInfo);
        btn_asset_cancel= (Button) findViewById(R.id.btn_cancel);

//        tv_selectInfo= (TextView) findViewById(R.id.tv_selectInfo);

        listView_asset_list= (ListView) findViewById(R.id.listView_asset_list);

        assetManualBatchListAdapter= new AssetManualBatchListAdapter(this, new ArrayList<AssetModel>());
        listView_asset_list.setAdapter(assetManualBatchListAdapter);

        //查询
        btn_asset_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assetQuery();
            }
        });

//        listView_asset_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Integer aaa=assetManualBatchListAdapter.getMapState().size();
//                Toast.makeText(getApplicationContext(),"总共选择==>"+aaa.toString(),Toast.LENGTH_SHORT).show();
//            }
//        });

        //保存
        btn_asset_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                HashMap<Integer, Boolean> state =assetManualBatchListAdapter.mapState;//.mapState();
                String options="选择的项是:";
                Iterator iter = state.entrySet().iterator();
                int orderNum=0;
                ArrayList<AssetModel> assetCodeList=new ArrayList<AssetModel>();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();

                    AssetModel assetModel=(AssetModel)assetManualBatchListAdapter.getItem(Integer.valueOf(key.toString()));

                    if (val.toString().equals("false")) continue;
                    if (assetModel==null) continue;

                    orderNum++;
                    Log.i("log","key===>"+key+" value ="+val.toString());

                    options+="\n"+String.valueOf(orderNum)+". "+assetModel.getAssetCode()+"\t"+assetModel.getAssetName();

                    assetCodeList.add(assetModel);

                }
                //显示选择内容

                if (assetCodeList.size()>0) {
                    Toast.makeText(getApplicationContext(), options, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "请选择盘点项", Toast.LENGTH_LONG).show();
                    return ;
                }
                v.setVisibility(View.INVISIBLE);
                if (AppContext.offlineLogin) {//离线登录
                    saveAssetBatchInfo(assetCodeList);
                }else{
                    saveOnlineAssetBatchInfo(assetCodeList);
                }

                v.setVisibility(View.VISIBLE);

            }
        });

//
        btn_asset_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                setResult(RESULT_CODE, intent);

                finish();
            }
        });
//
//        listView_asset_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String text = listView_asset_list.getItemAtPosition(position)+"";
//                Toast.makeText(getApplicationContext(), "position->" + position + "\ntext->" +text,Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void assetQuery() {

//        listView_asset_list.removeAllViews();

        if (TextUtils.isEmpty(edit_assetid.getText())){
            Toast.makeText(getApplicationContext(),"请输入查询信息",Toast.LENGTH_SHORT).show();
            return;
        }
        //查询信息
        if (AppContext.offlineLogin){//离线登录
            assetOfflineQuery();
        }else{//在线登录
            assetOnlineQuery();
        }
    }


    private void assetOfflineQuery(){
        hashMap = new HashMap<String,String>();
        String assetCode=edit_assetid.getText().toString();
//        String category="02";
        hashMap.put("assetOrgan",assetOrgan);
        hashMap.put("assetCode",assetCode);
//        HashMap.put("category",category);//按编码排序

        System.out.println(hashMap);
        try {
            List<AssetModel> assetList = dbManager.findOfflineAssetsWhereCodeOrName(getHelper().getAssetDao(), hashMap);
            if (assetList.size() == 0) {
                Toast.makeText(this, "没有搜索到查询结果...", Toast.LENGTH_SHORT).show();
                assetManualBatchListAdapter.addAll(new ArrayList<AssetModel>());
                tv_countInfo.setText("没有搜索到查询结果");
//                mProgressBar.setVisibility(View.GONE);
            } else {
//                System.out.println(assetList.get(0).getFinCode() + "| " + assetList.get(0).getAssetCode() + "| " + assetList.get(0).getCateName() + "| " +
//                        assetList.get(0).getMgrOrganCode());
                //排序，集合
//                if (hashMap.get("category").toString().equals("01")) {
//                    Collections.sort(assetList, new Comparator<AssetModel>() {
//                        @Override
//                        public int compare(AssetModel t2, AssetModel t1) {
//                            return t2.getFinCode().compareTo(t1.getFinCode());
//                        }
//                    });
//                } else {
//                    Collections.sort(assetList, new Comparator<AssetModel>() {
//                        @Override
//                        public int compare(AssetModel t2, AssetModel t1) {
//                            return t2.getAssetCode().compareTo(t1.getAssetCode());
//                        }
//                    });
//                }

                assetManualBatchListAdapter.addAll(assetList);

//                mProgressBar.setVisibility(View.GONE);

                tv_countInfo.setText("已加载" + assetList.size() + "条-共" + assetList.size() + "条");
//                tv_selectInfo.setText("你已选中["+assetManualBatchListAdapter.getCheckCount().get("selectCount")+"]项");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void assetOnlineQuery(){
        hashMap = new HashMap();

        String assetCode=edit_assetid.getText().toString();

        hashMap.put("methodName", "searchAssetBatchList");
        hashMap.put("organCode",assetOrgan);
        hashMap.put("assetCode",assetCode);

        searchAssetBatchListBySOAP(hashMap);
    }

    /**
     * 登录按钮要执行的命令
     * @param hashMap
     */
    private void searchAssetBatchListBySOAP(final HashMap hashMap) {

        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        //发送请求
        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                //返回后台数据
                response = TransUtil.decode(sa.getResponse(),hashMap);

                if (!response.equals("error")) {

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(response);

                    Log.i("log",jsonObject.toString());

                    int success = jsonObject.get("success").getAsInt();
//                    int recordCount = jsonObject.get("recordcount").getAsInt();
                    // 服务器无返回值
                    if (success == 2) {
                        Toast.makeText(getApplicationContext(), "服务器无响应，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }else if (success == 1) {// 获取数据成功
                        JsonArray assetList= jsonObject.get("list").getAsJsonArray();

                        loadOnlineBaseData(assetList);//加载使用部门信息
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "无法连接到服务器,请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 在线查询数据
     * @param assetList
     */
    private void loadOnlineBaseData(JsonArray assetList){

        if (assetList.size() == 0) {
            Toast.makeText(this, "没有搜索到查询结果...", Toast.LENGTH_SHORT).show();
            assetManualBatchListAdapter.addAll(new ArrayList<AssetModel>());
            tv_countInfo.setText("没有搜索到查询结果");
        } else {
            //排序，集合
//                if (hashMap.get("category").toString().equals("01")) {
//                    Collections.sort(assetList, new Comparator<AssetModel>() {
//                        @Override
//                        public int compare(AssetModel t2, AssetModel t1) {
//                            return t2.getFinCode().compareTo(t1.getFinCode());
//                        }
//                    });
//                } else {
//                    Collections.sort(assetList, new Comparator<AssetModel>() {
//                        @Override
//                        public int compare(AssetModel t2, AssetModel t1) {
//                            return t2.getAssetCode().compareTo(t1.getAssetCode());
//                        }
//                    });
//                }
            ArrayList<AssetModel> arrayList=new ArrayList<>();
            for (int i=0;i<assetList.size();i++){
                JsonObject jsonObject=assetList.get(i).getAsJsonObject();
                AssetModel model=new AssetModel(jsonObject);
                arrayList.add(model);
            }

            assetManualBatchListAdapter.addAll(arrayList);

            tv_countInfo.setText("已加载" + arrayList.size() + "条-共" + assetList.size() + "条");
        }
    }

    /**
 * 离线批量保存信息
 */
    private void saveAssetBatchInfo( ArrayList<AssetModel> assetCodeList){
        try {
            for (AssetModel tempAsset:assetCodeList) {

                tempAsset.setAddr(AppContext.address);
                tempAsset.setSimId(AppContext.simId);
                tempAsset.setUserId(AppContext.currUser.getAccounts());
                tempAsset.setMgrOrganCode(AppContext.currOrgan.getOrganCode());
//                assetModel.setOrganName(assetModel.getOrganName());
                tempAsset.setOfflineInv(true);//离线盘点资产

                ExtDate nowdate = new ExtDate();
                tempAsset.setPdate(nowdate.format("yyyy-MM-dd HH:mm:ss SSS"));
                tempAsset.setPdfs("2");//手工盘点

                dbManager.saveOfflineInvAssets(getHelper().getAssetDao(), tempAsset, tempAsset.getOrganCode());
            }
            Toast.makeText(getApplicationContext(), "数据保存成功", Toast.LENGTH_LONG).show();

            assetQuery();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //在线保存数据
    private  void saveOnlineAssetBatchInfo(ArrayList<AssetModel> assetList){
        for(AssetModel model:assetList){
            doInventory(model);
        }
    }


    public void doInventory(AssetModel assetModel) {

        String methodName = "doInventory";

        HashMap<String, String> hashMap = new HashMap<String, String>();
        assetModel.setSimId(AppContext.simId);
        assetModel.setAddr(AppContext.address);
//        assetModel.setMgrOrganCode(AppContext.currOrgan.getOrganCode());
        assetModel.setOrganCode(assetOrgan);

        hashMap.put("methodName", methodName);
        hashMap.put("organCode", assetOrgan);//organs.get(inventoryOrganSpinner.getSelectedItemPosition()).getOrganCode());
        hashMap.put("username", AppContext.currUser.getAccounts());
        hashMap.put("assetJson", this.getAssetJson(assetModel));

        Log.i("log","hashMap==>"+hashMap);

        loadDoInventoryInfo(hashMap);

        assetOnlineQuery();//重新加载

        Toast.makeText(getApplicationContext(),"保存成功", Toast.LENGTH_SHORT).show();
    }

    private void loadDoInventoryInfo(final HashMap hashMap) {
        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                response = TransUtil.decode(sa.getResponse(),hashMap);
                if (!response.equals("error")) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
                    String message = jsonObject.get("message").getAsString();
                    int success = jsonObject.get("success").getAsInt();
                    JsonObject asset = jsonObject.get("asset").getAsJsonObject();

//                    AssetModel assetModel = new AssetModel(asset);

                    if (success == 1) {
//                        assetListAdapter.replace(assetModel);

//                        message = "[" + assetModel.getAssetCode() + "] " + assetModel.getAssetName() + "盘点成功";

//                        DialogFragment dialogFragment = InvContinueDialogUtil.newInstance(message);
//
//                        dialogFragment.show(getFragmentManager(), "continue");
                    }
                } else {

//                    Toast.makeText(AssetInventory.this, "服务器请求失败,请重试...", Toast.LENGTH_SHORT).show();
//                    mProgressBar.setVisibility(View.GONE);

                }

            }
        });
    }


    public String getAssetJson(AssetModel assetModel) {
        String json;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson g = gsonBuilder.create();
        json = g.toJson(assetModel);
        return json;
    }

}


