package com.test.inventorysystem.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by youmengli on 6/7/16.
 */
@DatabaseTable(tableName = "organ")
public class OrganModel {

    @DatabaseField(id = true)
    private String pid;
    @DatabaseField
    private String userAccount;
    @DatabaseField
    private String organId;
    @DatabaseField
    private String organCode;
    @DatabaseField
    private String organType;
    @DatabaseField
    private String organName;
    @DatabaseField
    private String shortName;
    @DatabaseField
    private String inUse;

    public OrganModel () { super(); }

    public OrganModel (JsonObject jsonObject) {
        super();
        setOrganId(jsonObject.get("organID").getAsString());
        setOrganCode(jsonObject.get("organCode").getAsString());
        setOrganName(jsonObject.get("organName").getAsString());
        setShortName(jsonObject.get("shortName").getAsString());
        setInUse(jsonObject.get("inUse").getAsString());
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

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganCode(String organCode) {
        this.organCode = organCode;
    }

    public String getOrganCode() {
        return organCode;
    }

    public void setOrganType(String organType) {
        this.organType = organType;
    }

    public String getOrganType() {
        return organType;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setInUse(String inUse) {
        this.inUse = inUse;
    }

    public String getInUse() {
        return inUse;
    }
}
