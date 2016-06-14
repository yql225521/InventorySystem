package com.test.inventorysystem.models;

import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by youmengli on 6/8/16.
 */
@DatabaseTable(tableName = "type")
public class TypeModel {

    @DatabaseField(id = true)
    private String pid;
    @DatabaseField
    private String userAccount;
    @DatabaseField
    private String typeId;
    @DatabaseField
    private String typeCode;
    @DatabaseField
    private String typeType;
    @DatabaseField
    private String typeName;
    @DatabaseField
    private String cateFlags;

    public TypeModel () { super(); }

    public TypeModel (JsonObject object) {
        setTypeId(object.get("ID").getAsString());
        setTypeCode(object.get("code").getAsString());
        setTypeType(object.get("type").getAsString());
        setTypeName(object.get("name").getAsString());
//        System.out.println(object.get("cateFlags"));
        if (object.get("cateFlags") != null) {
            setCateFlags(object.get("cateFlags").getAsString());
        }
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeType(String typeType) {
        this.typeType = typeType;
    }

    public String getTypeType() {
        return typeType;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setCateFlags(String cateFlags) {
        this.cateFlags = cateFlags;
    }

    public String getCateFlags() {
        return cateFlags;
    }
}
