package com.test.inventorysystem.utils;

/**
 * Created by youmengli on 2016-05-31.
 */


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.Resources.NotFoundException;

public class PropertiesUtil {
    public InputStream FileStream;

    public PropertiesUtil(InputStream _FileStream) {
        this.FileStream = _FileStream;
    }

    public String Read(String name) {
        String Result = null;
        Properties p = new Properties();
        try {
            p.load(FileStream);
            Result = p.getProperty(name);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Result;
    }

    public Boolean Write(String name, String value) {
        Boolean Result = false;
        Properties p = new Properties();
        try {
            p.load(FileStream);
            Result = (Boolean) p.setProperty(name, value);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Result;
    }
}
