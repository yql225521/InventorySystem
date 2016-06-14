package com.test.inventorysystem.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.models.TypeModel;
import com.test.inventorysystem.models.UserModel;

import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by youmengli on 6/6/16.
 */

public class DBManager {

    public void saveUser (Dao<UserModel, String> userDao, String accounts, UserModel user, String username, String isValid, String departmentId) throws SQLException{
        System.out.println("用户创建.....");
        user.setAccounts(accounts);
        user.setUsername(username);
//        user.setPassword(password);
        user.setIsValid(isValid);
        user.setDepartmentId(departmentId);
        userDao.createOrUpdate(user);
    }

    public String findUser (Dao<UserModel, String> userDao, String userId) throws SQLException {
        System.out.println("搜索用户....");
        QueryBuilder<UserModel,String> queryBuilder = userDao.queryBuilder();
        queryBuilder.where().eq("accounts", userId);
        UserModel u1 = queryBuilder.queryForFirst();
        if (u1 == null) {
            return "no query result";
        } else {
            return u1.toString();
        }
    }

    public void deleteOrgans(Dao<OrganModel,String> organDao, String userAccount) throws SQLException{
        DeleteBuilder<OrganModel, String> delBuilder = organDao.deleteBuilder();
        delBuilder.delete();
    }

    public void saveOrganList (Dao<OrganModel, String> organDao, String userAccount, ArrayList<OrganModel> organs) throws SQLException {
        this.deleteOrgans(organDao, userAccount);
        for (OrganModel organModel : organs) {
            organModel.setUserAccount(userAccount);
            if(StringUtils.isBlank(organModel.getOrganType())){
                organModel.setOrganType("0");
            }
            organModel.setPid(userAccount + "_" + organModel.getOrganId());
            organDao.createOrUpdate(organModel);
        }
    }

    public List<OrganModel> findOrgans(Dao<OrganModel, String> organDao, String userAccount, String organType) throws SQLException{
        Map<String,Object> fmap=new HashMap<String,Object>();
        fmap.put("userAccount", userAccount);
        if(StringUtils.isBlank(organType)){
            fmap.put("organType", "0");
        }else{
            fmap.put("organType", organType);
        }
        return organDao.queryForFieldValues(fmap);
    }

    public void deleteTypes(Dao<TypeModel, String> typeDao, String userAccount) throws SQLException{
        DeleteBuilder<TypeModel,String> delBuilder = typeDao.deleteBuilder();
        delBuilder.where().eq("userAccount", userAccount).prepare();
        delBuilder.delete();
    }

    public void saveTypeList(Dao<TypeModel,String> typeDao, String userAccount, ArrayList<TypeModel> types) throws SQLException {
        this.deleteTypes(typeDao, userAccount);
        for (TypeModel typeModel : types) {
            typeModel.setUserAccount(userAccount);
            typeModel.setPid(userAccount + "_" + typeModel.getTypeId());// 手机库id
            typeDao.createOrUpdate(typeModel);
        }
    }

    public List<TypeModel> findTypes(Dao<TypeModel,String> typeDao, String userAccount, String typeType) throws SQLException{
        Map<String,Object> fmap=new HashMap<String,Object>();
        fmap.put("userAccount", userAccount);
        fmap.put("typeType", typeType);
        return typeDao.queryForFieldValues(fmap);
     }
}
