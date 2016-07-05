package com.test.inventorysystem.services;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.test.inventorysystem.interfaces.CallbackInterface;

/**
 * Created by youmengli on 6/2/16.
 */

public class SOAPActions {
    // create new SOAP request
    private String xmlRequest_header = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.assetmgr.rstco.com/\">" +
            "<soapenv:Header/>" + "<soapenv:Body>";
    private String xmlRequest_body = "";
    private String xmlRequest_rear = "</soapenv:Body>" + "</soapenv:Envelope>";
    private String xmlRequest = "";
    private String methodName = "";

    // http services parameters
    private String soapAction = "";
    private String serviceUrl = "http://192.168.0.3:8088/assetmgr/ws/DataAccess?wsdl";
    private String nameSpace = "http://ws.assetmgr.rstco.com/";
    //final response get from server side
    private String response = "";

    public SOAPActions(HashMap hashMap) {
        this.checkSOAPActions(hashMap);
    }

    public void checkSOAPActions(HashMap hashMap) {
        this.methodName = hashMap.get("methodName").toString();
        switch (this.methodName) {
            case "doLogin":
                this.doLogin(hashMap);
                break;
            case "getAssetInventoryBase":
                this.loadBaseData(hashMap);
                break;
            case "searchAssetList":
                this.searchAssetList(hashMap);
                break;
            case "getAssetInfoWithInv":
                this.getAssetInfo(hashMap);
                break;
            case "doInventory":
                this.doInventory(hashMap);
                break;
            case "searchInventoryAssetList":
                this.inventorySearchList(hashMap);
                break;
        }
    }

    private void doLogin(HashMap hashMap) {
        String username = hashMap.get("username").toString();
        String password = hashMap.get("password").toString();
        String addr = hashMap.get("addr").toString();
        String simId = hashMap.get("simId").toString();
        this.xmlRequest_body = "<ws:doLogin><username>" + username + "</username><password>" + password + "</password><addr>" +
                addr + "</addr><simId>" + simId + "</simId></ws:doLogin>";
        this.setHttpRequest(this.nameSpace, this.methodName, this.xmlRequest_header, this.xmlRequest_body, this.xmlRequest_rear);
    }

    private void loadBaseData(HashMap hashMap) {
        String organCode = hashMap.get("organCode").toString();
        this.xmlRequest_body = "<ws:getAssetInventoryBase><organCode>" + organCode + "</organCode></ws:getAssetInventoryBase>";
        this.setHttpRequest(this.nameSpace, this.methodName, this.xmlRequest_header, this.xmlRequest_body, this.xmlRequest_rear);
    }

    private void searchAssetList(HashMap hashMap) {
        String assetName = hashMap.get("assetName").toString();
        String assetCode = hashMap.get("assetCode").toString();
        String organCode = hashMap.get("organCode").toString();
        String category = hashMap.get("category").toString();
        String pageNo = hashMap.get("pageNo").toString();
        String pageSize = hashMap.get("pageSize").toString();
        this.xmlRequest_body = "<ws:searchAssetList><assetName>" + assetName + "</assetName><category>" + category + "</category><assetCode>" +
                assetCode + "</assetCode><organCode>" + organCode + "</organCode><pageNo>" + pageNo + "</pageNo><pageSize>" + pageSize +
                "</pageSize></ws:searchAssetList>";
        this.setHttpRequest(this.nameSpace, this.methodName, this.xmlRequest_header, this.xmlRequest_body, this.xmlRequest_rear);
    }

    private void getAssetInfo(HashMap hashMap) {
        String assetCode = hashMap.get("assetCode").toString();
        this.xmlRequest_body = "<ws:getAssetInfoWithInv><assetCode>" + assetCode + "</assetCode></ws:getAssetInfoWithInv>";
        this.setHttpRequest(this.nameSpace, this.methodName, this.xmlRequest_header, this.xmlRequest_body, this.xmlRequest_rear);
    }

    private void doInventory(HashMap hashMap) {
        String organCode = hashMap.get("organCode").toString();
//        String mgrOrganCode = hashMap.get("mgrOrganCode").toString();
        String username = hashMap.get("username").toString();
//        String assetCode = hashMap.get("assetCode").toString();
//        String addr = hashMap.get("addr").toString();
//        String simId = hashMap.get("simId").toString();
//        String disCodes = hashMap.get("disCodes").toString();
        String asset = hashMap.get("asset").toString();
//        this.xmlRequest_body = "<ws:doInventory><organCode>" + organCode + "</organCode><mgrOrganCode>" + mgrOrganCode +
//                "</mgrOrganCode><username>" + username + "</username><assetCode>" + assetCode + "</assetCode><addr>" + addr +
//                "</addr><simId>" + simId + "</simId><disCodes>" + disCodes + "</disCodes></ws:doInventory>";
        this.xmlRequest_body = "<ws:doInventory><username>" + username + "</username><organCode>" + organCode + "</organCode><assetJson>" + asset +
                "</assetJson></ws:doInventory>";
        this.setHttpRequest(this.nameSpace, this.methodName, this.xmlRequest_header, this.xmlRequest_body, this.xmlRequest_rear);
    }

    private void inventorySearchList(HashMap hashMap) {
        String organCode = hashMap.get("organCode").toString();
        String category = hashMap.get("category").toString();
        String storage = hashMap.get("storage").toString();
        String complete = hashMap.get("complete").toString();
        String storageMatchType = hashMap.get("storageMatchType").toString();
        String pageNo = hashMap.get("pageNo").toString();
        String pageSize = hashMap.get("pageSize").toString();
        this.xmlRequest_body = "<ws:searchInventoryAssetList><category>" + category + "</category><organCode>" + organCode +
                "</organCode><storage>" + storage + "</storage><storageMatchType>" + storageMatchType + "</storageMatchType><complete>" +
                complete + "</complete><pageNo>" + pageNo + "</pageNo><pageSize>" + pageSize + "</pageSize></ws:searchInventoryAssetList>";
        this.setHttpRequest(this.nameSpace, this.methodName, this.xmlRequest_header, this.xmlRequest_body, this.xmlRequest_rear);
    }

    public void sendRequest(Context ctx, String xmlRequest, final CallbackInterface callback) {

        AsyncHttpClient client = new AsyncHttpClient();

        String contentType = "text/xml; charset=utf-8";
        StringEntity entity = new StringEntity(xmlRequest, "UTF-8");
        entity.setContentType(contentType);
        entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, contentType));
        client.addHeader("Content-Type", contentType);
        client.addHeader("SOAPAction", this.soapAction);

        client.post(ctx, this.serviceUrl, entity, contentType, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("获取数据成功...");
                String res = new String(responseBody);
                response = res.substring(res.indexOf("<return>") + 8, res.indexOf("</return>"));
                callback.callBackFunction();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("服务器请求失败,没有返回值...");
                System.out.println(error);
            }
        });
    }

    private void setHttpRequest(String nameSpace, String methodName, String xmlRequest_header, String xmlRequest_body, String xmlRequest_tail) {
        this.soapAction = nameSpace + ":" + methodName;
        this.xmlRequest = xmlRequest_header + xmlRequest_body + xmlRequest_tail;
    }

    public String getXmlRequest() {
        return this.xmlRequest;
    }

    public String getResponse() {
        return this.response;
    }

}
