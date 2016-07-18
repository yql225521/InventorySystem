package com.test.inventorysystem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.models.CfgModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.models.TypeModel;
import com.test.inventorysystem.models.UserModel;

import java.sql.SQLException;

/**
 * Created by youmengli on 6/6/16.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "inventorySystem.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private Dao<UserModel, String> userDao = null;
    private Dao<OrganModel, String> organDao = null;
    private Dao<TypeModel, String> typeDao = null;
    private Dao<AssetModel, String> assetDao = null;
    private Dao<CfgModel, String> cfgDao = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.e(DBHelper.class.getName(), "开始创建数据库");
            TableUtils.createTable(connectionSource, UserModel.class);
            TableUtils.createTable(connectionSource, OrganModel.class);
            TableUtils.createTable(connectionSource, TypeModel.class);
            TableUtils.createTable(connectionSource, AssetModel.class);
            TableUtils.createTable(connectionSource, CfgModel.class);
            Log.e(DBHelper.class.getName(), "创建数据库成功");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DBHelper.class.getName(), "创建数据库失败");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, UserModel.class, true);
            TableUtils.dropTable(connectionSource, OrganModel.class, true);
            TableUtils.dropTable(connectionSource, TypeModel.class, true);
            TableUtils.dropTable(connectionSource, AssetModel.class, true);
            TableUtils.dropTable(connectionSource, CfgModel.class, true);
            onCreate(sqLiteDatabase, connectionSource);
            Log.e(DBHelper.class.getName(), "更新数据库成功");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(DBHelper.class.getName(), "更新数据库失败");
        }
    }

    public Dao<UserModel, String> getUserDao() throws SQLException{
        if (userDao == null) {
            userDao = getDao(UserModel.class);
        }
        return userDao;
    }

    public Dao<OrganModel, String> getOrganDao() throws SQLException {
        if (organDao == null) {
            organDao = getDao(OrganModel.class);
        }
        return organDao;
    }

    public Dao<TypeModel, String> getTypeDao() throws SQLException {
        if (typeDao == null) {
            typeDao = getDao(TypeModel.class);
        }
        return typeDao;
    }

    public Dao<AssetModel, String> getAssetDao() throws SQLException {
        if (assetDao == null) {
            assetDao = getDao(AssetModel.class);
        }
        return assetDao;
    }

    public Dao<CfgModel, String> getCfgDao() throws SQLException {
        if (cfgDao == null) {
            cfgDao = getDao(CfgModel.class);
        }
        return cfgDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        userDao = null;
        organDao = null;
        typeDao = null;
        assetDao = null;
        cfgDao = null;
    }
}
