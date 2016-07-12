package com.test.inventorysystem.utils;

import com.test.inventorysystem.models.OrganModel;
import com.test.inventorysystem.models.UserModel;

/**
 * Created by youmengli on 6/17/16.
 */

public class AppContext {
    public static UserModel currUser = null;
    public static OrganModel currOrgan = null;
    public static String address = "none";
    public static String simId = "460024065533470";

    public static Boolean offlineLogin = false;
}

