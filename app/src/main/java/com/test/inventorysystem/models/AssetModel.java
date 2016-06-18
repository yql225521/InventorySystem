package com.test.inventorysystem.models;

import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by youmengli on 6/12/16.
 */

@DatabaseTable(tableName = "asset")
public class AssetModel {

    @DatabaseField(id = true)
    private String pid;
    @DatabaseField
    private String assetName;
    @DatabaseField
    private String enableDateString;
    @DatabaseField
    private String status;
    @DatabaseField
    private int useAge;
    @DatabaseField
    private String mgrOrganName;
    @DatabaseField
    private Double originalValue;
    @DatabaseField
    private String assetCode;
    @DatabaseField
    private String storageDescr;
    @DatabaseField
    private String operator;
    @DatabaseField
    private String findCode;
    @DatabaseField
    private String assetTypeName;
    @DatabaseField
    private String assetId;
    @DatabaseField
    private String cateId;
    @DatabaseField
    private String organName;
    @DatabaseField
    private String cateName;
    @DatabaseField
    private String invMsg;
    @DatabaseField
    private String pdfs;
    @DatabaseField
    private String disCode;

    public AssetModel () { super(); }

    public AssetModel (JsonObject jsonObject) {
        super();
        setAssetName(jsonObject.get("assetName").getAsString());
        setEnableDateString(jsonObject.get("enableDateString").getAsString());
        setStatus(jsonObject.get("status").getAsString());
        setUseAge(jsonObject.get("useAge").getAsInt());
        setMgrOrganName(jsonObject.get("mgrOrganName").getAsString());
        setOriginalValue(jsonObject.get("originalValue").getAsDouble());
        setAssetCode(jsonObject.get("assetCode").getAsString());
        setStorageDescr(jsonObject.get("storageDescr").getAsString());
        setOperator(jsonObject.get("operator").getAsString());
        setFindCode(jsonObject.get("findCode").getAsString());
        setAssetTypeName(jsonObject.get("assetTypeName").getAsString());
        setAssetId(jsonObject.get("assetID").getAsString());
        setCateId(jsonObject.get("cateId").getAsString());
        setOrganName(jsonObject.get("organName").getAsString());
        setCateName(jsonObject.get("cateName").getAsString());
    }

    // if current object is inventory result
    public AssetModel (JsonObject jsonObject, String string) {
        super();
        setAssetName(jsonObject.get("assetName").getAsString());
        setEnableDateString(jsonObject.get("enableDateString").getAsString());
        setStatus(jsonObject.get("status").getAsString());
        setUseAge(jsonObject.get("useAge").getAsInt());
        setMgrOrganName(jsonObject.get("mgrOrganName").getAsString());
        setOriginalValue(jsonObject.get("originalValue").getAsDouble());
        setAssetCode(jsonObject.get("assetCode").getAsString());
        setStorageDescr(jsonObject.get("storageDescr").getAsString());
        setOperator(jsonObject.get("operator").getAsString());
        setFindCode(jsonObject.get("finCode").getAsString());
        setAssetTypeName(jsonObject.get("assetTypeName").getAsString());
        setOrganName(jsonObject.get("organName").getAsString());
        setCateName(jsonObject.get("cateName").getAsString());
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setEnableDateString(String enableDateString) {
        this.enableDateString = enableDateString;
    }

    public String getEnableDateString() {
        return enableDateString;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setUseAge(int useAge) {
        this.useAge = useAge;
    }

    public int getUseAge() {
        return useAge;
    }

    public void setMgrOrganName(String mgrOrganName) {
        this.mgrOrganName = mgrOrganName;
    }

    public String getMgrOrganName() {
        return mgrOrganName;
    }

    public void setOriginalValue(Double originalValue) {
        this.originalValue = originalValue;
    }

    public Double getOriginalValue() {
        return originalValue;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setStorageDescr(String storageDescr) {
        this.storageDescr = storageDescr;
    }

    public String getStorageDescr() {
        return storageDescr;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public void setFindCode(String findCode) {
        this.findCode = findCode;
    }

    public String getFindCode() {
        return findCode;
    }

    public void setAssetTypeName(String assetTypeName) {
        this.assetTypeName = assetTypeName;
    }

    public String getAssetTypeName() {
        return assetTypeName;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public String getCateId() {
        return cateId;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCateName() {
        return cateName;
    }

    public void setInvMsg(String invMsg) {
        this.invMsg = invMsg;
    }

    public String getInvMsg() {
        return invMsg;
    }

    public void setPdfs(String pdfs) {
        this.pdfs = pdfs;
    }

    public String getPdfs() {
        return pdfs;
    }

    public void setDisCode(String disCode) {
        this.disCode = disCode;
    }

    public String getDisCode() {
        return disCode;
    }
}
