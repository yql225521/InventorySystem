package com.test.inventorysystem.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.test.inventorysystem.R;
import com.test.inventorysystem.db.DBManager;
import com.test.inventorysystem.db.DBHelper;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.CfgModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.models.TypeModel;
import com.test.inventorysystem.models.UserModel;
import com.test.inventorysystem.services.SOAPActions;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.Sysconfig;
import com.test.inventorysystem.utils.TransUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login extends OrmLiteBaseActivity<DBHelper> {

    private EditText loginUsr = null;
    private EditText loginPwd = null;
    private TextView loginTips = null;
    private ProgressBar loginProgressBar = null;
    private boolean offline_flag = false;

    private String response = "";

    private DBManager dbManager = new DBManager();
    private UserModel loginUser;
    private OrganModel loginOrgan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initialization();
    }

    private void Initialization() {
        Sysconfig.getInstance(this);
        AppContext.offlineLogin = false;
        this.loginUsr = (EditText) findViewById(R.id.editText_usr);
        this.loginPwd = (EditText) findViewById(R.id.editText_pwd);
        this.loginTips = (TextView) findViewById(R.id.textView_login_tips);
        this.loginProgressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        final Button loginBtn = (Button) findViewById(R.id.button_login);
        Button loginOffBtn = (Button) findViewById(R.id.button_login_offlilne);
        ImageButton deleteUsrBtn = (ImageButton) findViewById(R.id.imageButton_del_usr);
        ImageButton deletePwdBtn = (ImageButton) findViewById(R.id.imageButton_del_pwd);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginTips.setVisibility((View.VISIBLE));
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("methodName", "doLogin");
                hashMap.put("username", loginUsr.getText().toString());
                hashMap.put("password", loginPwd.getText().toString());
                hashMap.put("addr", AppContext.address);

                TelephonyManager tm = (TelephonyManager) Login.this.getSystemService(Context.TELEPHONY_SERVICE);
                AppContext.simId = tm.getSubscriberId();
                hashMap.put("simId", AppContext.simId);
                doLogin(hashMap);
            }
        });

        loginOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginTips.setVisibility(View.VISIBLE);
                try {
                    UserModel loginUser = dbManager.findUser(getHelper().getUserDao(), loginUsr.getText().toString());
                    if (loginUser != null) {
                        loginOrgan = dbManager.findOrgan(getHelper().getOrganDao(), loginUser.getOrganCode());
                        System.out.println(loginOrgan.getOrganCode());
                        AppContext.offlineLogin = true;
                        AppContext.currUser = loginUser;
                        AppContext.currOrgan = loginOrgan;
                        TelephonyManager tm = (TelephonyManager) Login.this.getSystemService(Context.TELEPHONY_SERVICE);
                        AppContext.simId = tm.getSubscriberId();
                        CfgModel cfgModel = dbManager.findCfg(getHelper().getCfgDao(), loginUser.getAccounts(), "offline_flag");
                        if (cfgModel != null) {
                            AppContext.hasOfflineData = true;
                        }
                        buildMainFunction();
                    } else {
                        Toast.makeText(Login.this, "用户名错误", Toast.LENGTH_SHORT).show();
                        loginProgressBar.setVisibility(View.GONE);
                        loginTips.setVisibility(View.GONE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteUsrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUsr.setText("");
            }
        });

        deletePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginPwd.setText("");
            }
        });
    }

    private void doLogin(final HashMap hashMap) {
        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                response = TransUtil.decode(sa.getResponse());
                if (!response.equals("error")) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
                    System.out.println("user info " + jsonObject);
                    int success = jsonObject.get("success").getAsInt();

                    // 用户名或密码错误,服务器无返回值
                    if (success == 2) {
                        Toast.makeText(Login.this, "您输入的用户名或密码错误", Toast.LENGTH_SHORT).show();
                        loginProgressBar.setVisibility(View.GONE);
                    }
                    // 获取用户数据成功
                    else if (success == 1) {
                        JsonObject org = jsonObject.get("org").getAsJsonObject();
                        JsonObject user = jsonObject.get("user").getAsJsonObject();

                        initUser(user, org.get("organCode").getAsString());
                        initOrgan(org);

                        AppContext.address = "none";
                        AppContext.simId = hashMap.get("simId").toString();

                        //将当前登录用户和其所属管理部门信息存入本地数据,以便离线登录
                        try {
                            dbManager.saveUser(getHelper().getUserDao(), loginUser);
                            dbManager.saveOrgan(getHelper().getOrganDao(), loginOrgan, loginUser.getAccounts());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("methodName", "getAssetInventoryBase");
                        hashMap.put("organCode", org.get("organCode").getAsString());
                        loadBaseData(hashMap);
                    }
                } else {
                    Toast.makeText(Login.this, "无法连接到服务器,请重新登录", Toast.LENGTH_SHORT).show();
                    loginProgressBar.setVisibility(View.GONE);
                    loginTips.setVisibility(View.GONE);
                }

            }
        });
    }

    // 获取当前登录管理部门下所属的所有使用部门
    private void loadBaseData(HashMap hashMap) {
        final SOAPActions sa = new SOAPActions(hashMap);
        String xmlRequest = sa.getXmlRequest();

        sa.sendRequest(this, xmlRequest, new CallbackInterface() {
            @Override
            public void callBackFunction() {
                response = TransUtil.decode(sa.getResponse());
                if (!response.equals("error")) {
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = (JsonObject) jsonParser.parse(response);
                    System.out.println(jsonObject);

                    int success = jsonObject.get("success").getAsInt();
                    JsonArray organListString = jsonObject.get("organList").getAsJsonArray();
                    JsonArray typeListString = jsonObject.get("typeList").getAsJsonArray();
                    JsonArray matchTypeListString = jsonObject.get("matchTypeList").getAsJsonArray();
                    JsonArray completeTypeListString = jsonObject.get("completeTypeList").getAsJsonArray();

                    if (success == 1) {
                        try {
                            ArrayList<OrganModel> organList = new ArrayList<OrganModel>();
                            for (int i = 0; i < organListString.size(); i++) {
                                OrganModel organModel = new OrganModel(organListString.get(i).getAsJsonObject());
                                organList.add(organModel);
                            }
                            dbManager.saveOrganList(getHelper().getOrganDao(), AppContext.currUser.getAccounts(), organList);

                            ArrayList<TypeModel> typeList = new ArrayList<TypeModel>();
                            for (int i = 0; i < typeListString.size(); i++) {
                                TypeModel typeModel = new TypeModel(typeListString.get(i).getAsJsonObject());
                                typeList.add(typeModel);
                            }
                            for (int i = 0; i < matchTypeListString.size(); i++) {
                                TypeModel typeModel = new TypeModel(matchTypeListString.get(i).getAsJsonObject());
                                typeList.add(typeModel);
                            }
                            for (int i = 0; i < completeTypeListString.size(); i++) {
                                TypeModel typeModel = new TypeModel(completeTypeListString.get(i).getAsJsonObject());
                                typeList.add(typeModel);
                            }
                            dbManager.saveTypeList(getHelper().getTypeDao(), AppContext.currUser.getAccounts(), typeList);

                            buildMainFunction();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(Login.this, "服务器请求失败,请重试...", Toast.LENGTH_SHORT).show();
                    loginProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initUser(JsonObject user, String organCode) {
        loginUser = new UserModel();
        loginUser.setAccounts(user.get("accounts").getAsString());
        loginUser.setUsername(user.get("username").getAsString());
        loginUser.setDepartmentId(user.get("departmentId").getAsString());
        loginUser.setIsValid(user.get("isValid").getAsString());
        loginUser.setSecurityLevel(1);
        System.out.println(organCode);
        loginUser.setOrganCode(organCode);

        if (user.get("userId") != null) {
            loginUser.setUserId(user.get("userId").getAsString());
        }
        if (user.get("departmentName") != null) {
            loginUser.setDepartmentName(user.get("departmentName").getAsString());
        }
        if (user.get("employeeId") != null) {
            loginUser.setEmployeeId(user.get("employeeId").getAsString());
        }
        if (user.get("employeeName") != null) {
            loginUser.setEmployeeName(user.get("employeeName").getAsString());
        }

        AppContext.currUser = loginUser;
    }

    private void initOrgan(JsonObject organ) {
        loginOrgan = new OrganModel(organ);
        loginOrgan.setUserAccount(AppContext.currUser.getAccounts());
        System.out.println("login organ " + loginOrgan.getOrganCode());
        AppContext.currOrgan = loginOrgan;
    }

    private void buildMainFunction() {
        Intent toMainPage = new Intent(this, MainActivity.class);
        startActivity(toMainPage);
        this.finish();
    }
}
