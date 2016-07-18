package com.test.inventorysystem.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.test.inventorysystem.R;
import com.test.inventorysystem.adapters.MainListAdapter;
import com.test.inventorysystem.utils.AppContext;
import com.test.inventorysystem.utils.UserInfoDialogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String userName = "";
    private String userAccount = "";
    private String userDepartmentId = "";

    String[] mainGridTitles = {"资产查询", "资产盘点", "盘点情况", "资产扫描", "拍照上传", "离线盘点管理", "离线数据下载", "帮助"};
    int[] mainGridIcons = {R.drawable.ic_search_black_24dp, R.drawable.ic_local_atm_black_24dp,
            R.drawable.ic_content_paste_black_24dp, R.drawable.ic_crop_free_black_24dp,
            R.drawable.ic_camera_alt_black_24dp, R.drawable.ic_format_list_numbered_black_24dp,
            R.drawable.ic_vertical_align_bottom_black_24dp, R.drawable.ic_help_black_24dp};

    String[] offlineMainGridTitle = {"离线资产查询", "离线盘点", "离线扫描", "离线盘点管理", "离线数据下载", "帮助"};
    int[] offlineMainGridIcons = {R.drawable.ic_search_black_24dp, R.drawable.ic_local_atm_black_24dp, R.drawable.ic_crop_free_black_24dp,
            R.drawable.ic_format_list_numbered_black_24dp, R.drawable.ic_vertical_align_bottom_black_24dp, R.drawable.ic_help_black_24dp};

    List<String> mainItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialization();
    }

    private void Initialization() {
        GridView gridview = (GridView) findViewById(R.id.grid_view);
        if (!AppContext.offlineLogin) {
            gridview.setAdapter(new MainListAdapter(this, this.mainGridTitles, this.mainGridIcons));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    switch (position) {
                        case 0:
                            goToAssetSearch();
                            break;
                        case 1:
                            goToAssetInventory();
                            break;
                        case 2:
                            goToInventorySearch();
                            break;
                        case 3:
                            goToCodeScanner();
                            break;
                        case 5:
                            goToOfflineInventoryMgr();
                            break;
                        case 6:
                            goToDownloadOfflineData();
                            break;
                    }
                }
            });
        } else {
            gridview.setAdapter(new MainListAdapter(this, this.offlineMainGridTitle, this.offlineMainGridIcons));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    switch (position) {
                        case 0:
                            goToAssetSearch();
                            break;
                        case 1:
                            goToOfflineAssetInventory();
                            break;
                        case 2:
                            goToCodeScanner();
                            break;
                        case 3:
                            goToOfflineInventoryMgr();
                            break;
                        case 4:
                            goToDownloadOfflineData();
                            break;
                    }
                }
            });
        }
    }

    private void goToAssetSearch () {
        Intent intent = new Intent(this, AssetSearch.class);
        startActivity(intent);
    }

    private void goToAssetInventory () {
        Intent intent = new Intent(this, AssetInventory.class);
//        intent.putExtra("userAccount", userAccount);
        startActivity(intent);
    }

    private void goToInventorySearch() {
        Intent intent = new Intent(this, InventorySearch.class);
        startActivity(intent);
    }

    private void goToCodeScanner() {
        Intent intent = new Intent(this, AssetQRShow.class);
        startActivity(intent);
    }

    private void goToOfflineAssetInventory() {
        Intent intent = new Intent(this, OfflineAssetInventory.class);
        startActivity(intent);
    }

    private void goToOfflineInventoryMgr() {
        Intent intent = new Intent(this, OfflineInventoryManager.class);
        startActivity(intent);
    }

    private void goToDownloadOfflineData() {
        Intent intent = new Intent(this, DownloadOfflineData.class);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_info:
                showUserInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUserInfo () {
        UserInfoDialogUtil userInfo = UserInfoDialogUtil.newInstance(AppContext.currUser);
        userInfo.show(getFragmentManager(), "user_info");
    }
}
