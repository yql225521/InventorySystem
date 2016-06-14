package com.test.inventorysystem.utils;

import android.content.Context;
import com.test.inventorysystem.R;


/**
 * Created by youmengli on 2016-05-31.
 */

public class Sysconfig {
    private static String ServiceUrl = null;
    private static String Verbz = null;
    private static String LocalAdm = null;
    private static String NameSpace = null;
    private static String AutoUpdateUrl = null;
    private static String ModelID = null;
    private static Context _context = null;
    private static String areaID = null;

    public static String getServiceUrl() {
        PropertiesUtil p = new PropertiesUtil(_context.getResources()
                .openRawResource(R.raw.config));
        ServiceUrl = p.Read("ServiceUrl");
        System.out.println(ServiceUrl);
        return ServiceUrl;
    }

    public static String getVerBz() {
        PropertiesUtil p = new PropertiesUtil(_context.getResources()
                .openRawResource(R.raw.config));
        Verbz = p.Read("Verbz");
        return Verbz;
    }


    /**
     * 设置机构代码
     *
     * @paramorginID
     */
    public static void setLocalAdm(String localAdm) {
        LocalAdm = localAdm;
        PreferencesUtil pu = new PreferencesUtil(_context);
        pu.Write("SysConfig", "LocalAdm", localAdm);
    }

    /**
     * 获取机构代码
     *
     * @return
     */
    public static String getLocalAdm() {
        PreferencesUtil pu = new PreferencesUtil(_context);
        LocalAdm = pu.Read("SysConfig", "LocalAdm");
        if (LocalAdm.equals("")) {
            PropertiesUtil p = new PropertiesUtil(_context.getResources()
                    .openRawResource(R.raw.config));
            LocalAdm = p.Read("LocalAdm");
            setLocalAdm(LocalAdm);
        }
        return LocalAdm;
    }
    /**
     * 设置服务命名空间
     *
     * @param nameSpace
     */
    public static void setNameSpace(String nameSpace) {
        NameSpace = nameSpace;
        PreferencesUtil pu = new PreferencesUtil(_context);
        pu.Write("SysConfig", "NameSpace", nameSpace);
    }

    /**
     * 获取命名空间
     *
     * @return
     */
    public static String getNameSpace() {
        PreferencesUtil pu = new PreferencesUtil(_context);
        NameSpace = pu.Read("SysConfig", "NameSpace");
        if (NameSpace.equals("")) {
            PropertiesUtil p = new PropertiesUtil(_context.getResources()
                    .openRawResource(R.raw.config));
            NameSpace = p.Read("NameSpace");
            setNameSpace(NameSpace);
        }
        return NameSpace;
    }

    /**
     * @param autoUpdateUrl
     *            the autoUpdateUrl to set
     */
    public static void setAutoUpdateUrl(String autoUpdateUrl) {
        AutoUpdateUrl = autoUpdateUrl;
        PreferencesUtil pu = new PreferencesUtil(_context);
        pu.Write("SysConfig", "AutoUpdateUrl", autoUpdateUrl);
    }

    /**
     * @return the autoUpdateUrl
     */
    public static String getAutoUpdateUrl() {
        PropertiesUtil p = new PropertiesUtil(_context.getResources()
                .openRawResource(R.raw.config));
        AutoUpdateUrl = p.Read("AutoUpdateUrl");
        return AutoUpdateUrl;
    }

    /**
     * @param modelID
     *            the modelID to set
     */
    public static void setModelID(String modelID) {
        ModelID = modelID;
        PreferencesUtil pu = new PreferencesUtil(_context);
        pu.Write("SysConfig", "ModelID", modelID);
    }

    /**
     * @return the modelID
     */
    public static String getModelID() {
        PreferencesUtil pu = new PreferencesUtil(_context);
        ModelID = pu.Read("SysConfig", "ModelID");
        if (ModelID.equals("")) {
            PropertiesUtil p = new PropertiesUtil(_context.getResources()
                    .openRawResource(R.raw.config));
            ModelID = p.Read("ModelID");
            setModelID(ModelID);
        }
        return ModelID;
    }

    /**
     * 获得组织机构ID
     * @return
     */
    public static String getAreaID(){
        PreferencesUtil pu = new PreferencesUtil(_context);
        areaID= "";
        return areaID;
    }
}
