package com.test.inventorysystem.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.test.inventorysystem.adapters.OfflineInvListAdapter;
import com.test.inventorysystem.interfaces.CallbackInterface;
import com.test.inventorysystem.models.AssetModel;
import com.test.inventorysystem.models.CfgModel;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.models.TypeModel;
import com.test.inventorysystem.models.UserModel;
import com.test.inventorysystem.utils.ExtDate;

import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by youmengli on 6/6/16.
 */

public class DBManager {

    public void saveUser(Dao<UserModel, String> userDao, UserModel user) throws SQLException {
        System.out.println("用户创建.....");
        userDao.createOrUpdate(user);
    }

    public UserModel findUser(Dao<UserModel, String> userDao, String userAccount) throws SQLException {
        QueryBuilder<UserModel, String> queryBuilder = userDao.queryBuilder();
        queryBuilder.where().eq("accounts", userAccount);
        UserModel u1 = queryBuilder.queryForFirst();
        if (u1 == null) {
            return null;
        } else {
            return u1;
        }
    }

    public void deleteOrgans(Dao<OrganModel, String> organDao, String userAccount) throws SQLException {
        DeleteBuilder<OrganModel, String> delBuilder = organDao.deleteBuilder();
        delBuilder.delete();
    }

    public void saveOrgan(Dao<OrganModel, String> organDao, OrganModel organModel, String userAccount) throws SQLException {
        organModel.setPid(userAccount + "_" + organModel.getOrganID());
        organDao.createOrUpdate(organModel);
        System.out.println("用户部门创建.....");
    }

    public void saveOrganList(Dao<OrganModel, String> organDao, String userAccount, ArrayList<OrganModel> organs) throws SQLException {
//        this.deleteOrgans(organDao, userAccount);
        for (OrganModel organModel : organs) {
            organModel.setUserAccount(userAccount);
            if (StringUtils.isBlank(organModel.getOrganType())) {
                organModel.setOrganType("0");
            }
            organModel.setPid(userAccount + "_" + organModel.getOrganID());
            organDao.createOrUpdate(organModel);
        }
    }

    public OrganModel findOrgan(Dao<OrganModel, String> organDao, String organCode) throws SQLException {
        QueryBuilder<OrganModel, String> queryBuilder = organDao.queryBuilder();
        queryBuilder.where().eq("organCode", organCode);
        OrganModel o1 = queryBuilder.queryForFirst();
        if (o1 == null) {
            return null;
        } else {
            return o1;
        }
    }

    public List<OrganModel> findOrgans(Dao<OrganModel, String> organDao, String userAccount, String organType) throws SQLException {
        Map<String, Object> fmap = new HashMap<String, Object>();
        fmap.put("userAccount", userAccount);
        if (StringUtils.isBlank(organType)) {
            fmap.put("organType", "0");
        } else {
            fmap.put("organType", organType);
        }
        return organDao.queryForFieldValues(fmap);
    }

    public void deleteTypes(Dao<TypeModel, String> typeDao, String userAccount) throws SQLException {
        DeleteBuilder<TypeModel, String> delBuilder = typeDao.deleteBuilder();
        delBuilder.where().eq("userAccount", userAccount).prepare();
        delBuilder.delete();
    }

    public void saveTypeList(Dao<TypeModel, String> typeDao, String userAccount, ArrayList<TypeModel> types) throws SQLException {
        this.deleteTypes(typeDao, userAccount);
        for (TypeModel typeModel : types) {
            typeModel.setUserAccount(userAccount);
            typeModel.setPid(userAccount + "_" + typeModel.getTypeId());// 手机库id
            typeDao.createOrUpdate(typeModel);
        }
    }

    public List<TypeModel> findTypes(Dao<TypeModel, String> typeDao, String userAccount, String typeType) throws SQLException {
        Map<String, Object> fmap = new HashMap<String, Object>();
        fmap.put("userAccount", userAccount);
        fmap.put("typeType", typeType);
        return typeDao.queryForFieldValues(fmap);
    }

    public void deleteAsset(Dao<AssetModel, String> assetDao) throws SQLException {
        DeleteBuilder<AssetModel, String> deleteBuilder = assetDao.deleteBuilder();
        deleteBuilder.delete();
    }

    public void saveOfflineInvAssets(Dao<AssetModel, String> assetDao, AssetModel assetModel, String organCode) throws SQLException {
        ExtDate nowdate = new ExtDate();
        String upid = organCode + "-" + nowdate.format("yyyyMMddHHmmssSSS");
        assetModel.setUpid(upid);
        assetModel.setOrganCode(organCode);
        assetModel.setOfflineInv(true);
        assetModel.setDt("1");
        assetModel.setPid(assetModel.getUserId() + "_" + assetModel.getAssetCode());
        assetDao.createOrUpdate(assetModel);
    }

    public synchronized String updateUpid(Dao<AssetModel, String> assetDao, String userId, String organCode) throws SQLException {
        UpdateBuilder<AssetModel, String> astUpdateBuilder = assetDao.updateBuilder();
        ExtDate nowdate = new ExtDate();
        String upid = organCode + "-" + nowdate.format("yyyyMMddHHmmssSSS");
        astUpdateBuilder.where().eq("organCode", organCode).and().eq("userId", userId);
        astUpdateBuilder.updateColumnValue("upid", upid);
        astUpdateBuilder.update();
        return upid;
    }

    public Boolean findExistedOfflineInvAsset(Dao<AssetModel, String> assetDao, String finCode) throws SQLException {
        QueryBuilder<AssetModel, String> queryBuilder = assetDao.queryBuilder();
        queryBuilder.where().eq("finCode", finCode).and().eq("offlineInv", true);
        AssetModel assetModel = queryBuilder.queryForFirst();
        if (assetModel != null) {
            return true;
        } else {
            return false;
        }
    }

    public List<AssetModel> findOfflineInvAssetsByOrgan(Dao<AssetModel, String> assetDao, String organCode, Boolean offline) throws SQLException {
        Map<String, Object> fmap = new HashMap<>();
        fmap.put("organCode", organCode);
        fmap.put("offlineInv", offline);
        return assetDao.queryForFieldValues(fmap);
    }

    public void deleteOfflineInvAssetsWithOrgan(Dao<AssetModel, String> assetDao, String userid, String organCode) throws SQLException {
        DeleteBuilder<AssetModel, String> delBuilder = assetDao.deleteBuilder();
        delBuilder.where().eq("userId", userid).and().eq("organCode", organCode).prepare();
        delBuilder.delete();
    }

    public void deleteOfflineAssets(Dao<AssetModel, String> assetDao) throws SQLException {
        DeleteBuilder<AssetModel, String> delBuilder = assetDao.deleteBuilder();
        delBuilder.where().eq("dt", "2").prepare();
        delBuilder.delete();
    }

    public void saveOfflineAssets(Dao<AssetModel, String> assetDao, List<AssetModel> assets, String userId, CallbackInterface callback) throws SQLException {
        for (AssetModel assetModel : assets) {
            assetModel.setUserId(userId);
            assetModel.setAddr("");
            assetModel.setSimId("");
            assetModel.setDt("2");
            assetModel.setPid("qty2_" + assetModel.getAssetCode());// 手机库id
            assetDao.createOrUpdate(assetModel);
        }
        callback.callBackFunction();
    }

    public AssetModel findOfflineAsset(Dao<AssetModel, String> assetDao, String finCode, String dt) throws SQLException{
        QueryBuilder<AssetModel, String> queryBuilder = assetDao.queryBuilder();
        queryBuilder.where().eq("finCode", finCode).and().eq("dt", dt);
        AssetModel assetModel = queryBuilder.queryForFirst();
        if (assetModel == null) {
            return null;
        } else  {
            return assetModel;
        }
    }

    public List<AssetModel> findOfflineAssets(Dao<AssetModel, String> assetDao, HashMap<String, String> hashMap) throws SQLException{
        QueryBuilder<AssetModel, String> queryBuilder = assetDao.queryBuilder();
        Where<AssetModel, String> where = queryBuilder.where();

        String assetCode = hashMap.get("assetCode");
        String assetName = hashMap.get("assetName");
        String organCode = hashMap.get("organCode");
        String category = hashMap.get("category");

        where.isNotNull("assetCode");
        if (StringUtils.isNotBlank(assetCode)) {
            where.and().eq("organCode", assetCode);
        }
        if (StringUtils.isNotBlank(assetName)) {
            where.and().like("assetName", "%" + assetName + "%");
        }
        if (StringUtils.isNotBlank(organCode)) {
            where.and().eq("organCode", organCode);
        }
        if (StringUtils.isNotBlank(category)) {
            where.and().eq("cateID", category);
        }
        ArrayList<AssetModel> assetList = new ArrayList<>(queryBuilder.query());
        System.out.println(assetList);
        return assetList;
    }

    public void deleteCfg(Dao<CfgModel, String> cfgDao, String userAccount) throws SQLException {
        DeleteBuilder<CfgModel, String> deleteBuilder = cfgDao.deleteBuilder();
        deleteBuilder.where().eq("userAccount", userAccount).prepare();
        deleteBuilder.delete();
    }

    public void saveCfg(Dao<CfgModel, String> cfgDao, CfgModel cfgModel, String userAccount, String name) throws SQLException {
        this.deleteCfg(cfgDao, userAccount);
        cfgModel.setCfgId(userAccount + "_" + name);
        cfgDao.createOrUpdate(cfgModel);
    }

    public CfgModel findCfg(Dao<CfgModel, String> cfgDao, String userAccount, String name) throws SQLException {
        return cfgDao.queryForId(userAccount + "_" + name);
    }
}
