package com.test.inventorysystem.utils;

import android.content.Context;
import com.test.inventorysystem.R;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by youmengli on 2016-05-31.
 */

public class Sysconfig {
    private static Sysconfig m_intance = null;
    private static String ServiceUrl = null;
    private static String Verbz = null;
    private static String LocalAdm = null;
    private static String NameSpace = null;
    private static String AutoUpdateUrl = null;
    private static String ModelID = null;
    private static String XMLRequestHeader = null;
    private static String XMLRequestRear = null;
    private static Context _context = null;
    private static String areaID = null;

    synchronized public static Sysconfig getInstance(Context c) {
        if (m_intance == null) {
            m_intance = new Sysconfig();
            _context = c;
        }
        return m_intance;
    }

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
        pu.Write("Sysconfig", "LocalAdm", localAdm);
    }

    /**
     * 获取机构代码
     *
     * @return
     */
    public static String getLocalAdm() {
        PreferencesUtil pu = new PreferencesUtil(_context);
        LocalAdm = pu.Read("Sysconfig", "LocalAdm");
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
        pu.Write("Sysconfig", "NameSpace", nameSpace);
    }

    /**
     * 获取命名空间
     *
     * @return
     */
    public static String getNameSpace() {
        PreferencesUtil pu = new PreferencesUtil(_context);
        NameSpace = pu.Read("Sysconfig", "NameSpace");
        if (NameSpace.equals("")) {
            PropertiesUtil p = new PropertiesUtil(_context.getResources()
                    .openRawResource(R.raw.config));
            NameSpace = p.Read("NameSpace");
            setNameSpace(NameSpace);
        }
        return NameSpace;
    }

    public static String getXMLRequestHeader() {
        PropertiesUtil p = new PropertiesUtil(_context.getResources()
                .openRawResource(R.raw.config));
        XMLRequestHeader = p.Read("XMLRequestHeader");
        return XMLRequestHeader;
    }

    public static String getXMLRequestRear() {
        PropertiesUtil p = new PropertiesUtil(_context.getResources()
                .openRawResource(R.raw.config));
        XMLRequestRear = p.Read("XMLRequestRear");
        return XMLRequestRear;
    }

    /**
     * @param autoUpdateUrl
     *            the autoUpdateUrl to set
     */
    public static void setAutoUpdateUrl(String autoUpdateUrl) {
        AutoUpdateUrl = autoUpdateUrl;
        PreferencesUtil pu = new PreferencesUtil(_context);
        pu.Write("Sysconfig", "AutoUpdateUrl", autoUpdateUrl);
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
        pu.Write("Sysconfig", "ModelID", modelID);
    }

    /**
     * @return the modelID
     */
    public static String getModelID() {
        PreferencesUtil pu = new PreferencesUtil(_context);
        ModelID = pu.Read("Sysconfig", "ModelID");
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

    public static String[] getCodes(String barcode) {
        if (!"".equals(barcode)) {
            if(StringUtils.indexOf(barcode, "\n")>-1){//晋中标签问题
                String[]  tas=barcode.split("\n");
                if(tas.length>=2&&"晋中市烟草公司".equals(tas[0])) {
                    List<String> tlst=new ArrayList();
                    tlst.add(tas[2]);//0
                    if(tas.length>3){
                        tlst.add(tas[3]);//1
                    }else{
                        tlst.add("");//1
                    }
                    if(tas.length>4){
                        tlst.add(tas[4]);//1
                    }else{
                        tlst.add("");//1
                    }
                    tlst.add(tas[1]);
                    System.out.println("asset:"+StringUtils.join(tlst,","));
                    return tlst.toArray(new String[tlst.size()]);
                }
                return tas;
            }else{
                return barcode.split("\t");
            }
        }

        return null;
    }
}
